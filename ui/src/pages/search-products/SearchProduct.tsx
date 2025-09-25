import {BaseRecord, CrudSort, IResourceComponentsProps, useNotification} from "@refinedev/core";
import {Button, Form, Image, Input, Modal, Slider, Table} from "antd";
import React, {useEffect, useState} from "react";
import {NumberField} from "@refinedev/antd";
import {dataProvide} from "../../App";
import {Link, useSearchParams} from "react-router-dom";
import {getDigikalaLink} from "../match-list/list";
import {AreaChartOutlined} from "@ant-design/icons";

export function SearchPreview({search}: SearchPreviewProps){
    return <div style={{padding: '10px 40px'}}>
        {search?.category && <div style={{padding: '10px'}}><span>Category: </span><span style={{fontWeight:'bold'}}>{search.category}</span> </div>}
        {search?.brand && <div style={{padding: '10px'}}><span>Brand:</span> <span style={{fontWeight:'bold'}}>{search.brand}</span> </div>}
        {search?.src && <div style={{padding: '10px'}}><span>Website:</span> <span style={{fontWeight:'bold'}}>{search.src}</span> </div>}
        {search?.roi && <div style={{padding: '10px'}}><span>Roi: </span><span style={{fontWeight:'bold'}}><NumberField value={search.roi.min * 100}/> - <NumberField value={search.roi.max* 100}/></span> </div>}
        {search?.profit && <div style={{padding: '10px'}}><span>Profit:</span> <span style={{fontWeight:'bold'}}><NumberField value={search.profit.min}/> - <NumberField value={search.profit.max}/></span> </div>}
        {search?.srcPrice && <div style={{padding: '10px'}}><span>Src Price:</span> <span style={{fontWeight:'bold'}}><NumberField value={search.srcPrice.min}/> - <NumberField value={search.srcPrice.max}/></span> </div>}
    </div>;
}
interface SearchPreviewProps {
    search: any
}

export function ImagePreview({images, width, style}: ImagePreviewProps) {
    const [visible, setVisible] = useState(false);

    function getImages() {
        return images && images.length > 0 ? images : [];
    }

    function getFirstImages() {
        return images && images.length > 0 ? images[0].link : undefined;
    }

    return (
        <>
            <Image
                preview={{visible: false}}
                width={width || 100}
                style={style}
                src={getFirstImages()}
                onClick={() => setVisible(true)}
            />
            <div style={{display: 'none'}}>
                <Image.PreviewGroup preview={{visible, onVisibleChange: vis => setVisible(vis)}}>
                    {getImages().map((image: any) => <Image alt={image.title} title={image.title} src={image.link}/>)}
                </Image.PreviewGroup>
            </div>
        </>
    );
}

export interface ImagePreviewProps {
    images: any[],
    width?: number | undefined,
    style?: object
}

export function convertFiltersToRequest(filters:any){
    const request:any = {};
    Object.keys(filters).forEach(key=>request[key]=filters[key]);
    request['roi'] = fixRangeFilter(filters, 'roi');
    request['profit'] = fixRangeFilter(filters, 'profit');
    request['srcPrice'] = fixRangeFilter(filters, 'srcPrice');
    request['dstPrice'] = fixRangeFilter(filters, 'dstPrice');
    return request;
}

export function convertFiltersToUrlParam(filters:any){
    const params:any = {};
    Object.keys(filters).filter(key=>filters[key]).forEach(key=>{
        let value  = filters[key];
        if(key ==='roi' || key === 'profit' || key==='srcPrice' || key === 'dstPrice'){
            value = [filters[key].min * getMultiplier(key), filters[key].max * getMultiplier(key)];
        }
        params[key] = value;
    });
    return params;
}
function getMultiplier(key:string):number{
    if(key ==='roi')
        return 100;
    return 1;
}
function fixRangeFilter(values: any, key: string) {
    if (values[key] && values[key].length === 2) {
        let min = values[key][0];
        let max = values[key][1];
        if (key === 'roi') {
            min = min.toFixed(2) / getMultiplier(key);
            max = max.toFixed(2) / getMultiplier(key);
        }
        return {min: min, max: max};
    }
    return null;
}


const preDefaultFilters = {status: 'Confirmed'}
export const SearchProduct: React.FC<IResourceComponentsProps> = () => {
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [searchName, setSearchName] = useState("");
    const [data, setData] = useState<BaseRecord>([]);
    const [pagination, setPagination] = useState<{ current: any, pageSize: any, total: any }>({
        current: 1,
        pageSize: 10,
        total: 0
    });
    const [filters, setFilters] = useState({profit: [-10000000, 10000000], roi: [-100,100], srcPrice:[0, 50000000], dstPrice: [0, 50000000]});
    const {open} = useNotification();
    const [sorter, setSorter] = useState<CrudSort>({
        field: 'created',
        order: 'desc',
    });

    const [loading, setLoading] = useState(true);

    const [searchParams, setSearchParams] = useSearchParams();


    // Sync URL with state
    // Sync state with URL on mount
    useEffect(() => {
        const page = searchParams.get("page") || "1";
        const pageSize = searchParams.get("pageSize") || "10";
        const filtersFromUrl = searchParams.get("filters");
        const sorterFromUrl = searchParams.get("sorter");

        setPagination({...pagination, current: parseInt(page), pageSize: parseInt(pageSize)});

        if (filtersFromUrl) {
            setFilters(JSON.parse(filtersFromUrl));
        }
        if (sorterFromUrl) {
            setSorter(JSON.parse(sorterFromUrl));
        }
        setLoading(false);
    }, []);


    useEffect(() => {
        // Fetch data based on the current pagination, sorter, and filters
        search(convertFiltersToRequest(filters));

        setSearchParams({
            page: pagination.current,
            pageSize: pagination.pageSize,
            filters: JSON.stringify(filters),
            sorter: JSON.stringify(sorter)
        });

    }, [pagination, sorter, filters]);

    function saveSearch(){
        setIsModalVisible(true);
    }
    const handleOk = () => {
        // Save the search with the given name
        const savedSearch = {
            name: searchName,
            search: convertFiltersToRequest(filters)
        };
        dataProvide.create({resource: 'user/saveSearch', variables: savedSearch}).then(() =>{
            setIsModalVisible(false);
        }).catch(()=>{
            setIsModalVisible(false);
        });

    };

    const handleCancel = () => {
        setIsModalVisible(false);
    };

    function search(values: any) {

        dataProvide.getList({
            resource: "matched",
            filters: {...values, ...preDefaultFilters},
            sorters: [sorter],
            pagination: pagination
        }).then(data => {
            setData(data.data);
            if (pagination.total !== data?.total)
                setPagination({...pagination, total: data?.total});
        }).catch(() => {
            open?.({
                type: "error",
                message: "Load Error",
                description: "Try again"
            })
        })
    }

    function handleFilters(formValues: any) {
        const values:any =[];
        const filters:any = {};
        Object.keys(formValues).forEach(key => values[key]=formValues[key]);


        Object.keys(values).filter(key => !values[key]).forEach(key => delete values[key]);
        Object.keys(values).forEach(key => filters[key]=values[key]);
        setFilters(filters);
    }

    return !loading?<div>
        <Form onFinish={handleFilters} initialValues={filters}>

            <div style={{display: 'flex'}}>
                <Form.Item style={{minWidth: '200px', marginRight: '10px'}} label={'Category'}
                           name={'category'}><Input defaultValue={filters.category}/></Form.Item>
                <Form.Item style={{minWidth: '200px',marginRight: '10px'}} label={'Brand'} name={'brand'}><Input/></Form.Item>
                <Form.Item style={{minWidth: '200px'}} label={'Src Website'} name={'src'}><Input/></Form.Item>
            </div>
            <div>
                <div style={{display: 'flex'}}>
                    <Form.Item style={{maxWidth: '300px', minWidth: '250px', marginRight: '10px'}} name={'roi'} label="ROI">
                        <Slider  range min={-100} max={100}/>
                    </Form.Item>
                    <Form.Item style={{maxWidth: '300px',minWidth: '250px', marginRight: '10px'}} name={'profit'} label="Profit">
                        <Slider range
                                tipFormatter={(value) => <NumberField style={{color: 'white'}} value={value}/>}
                                min={-10000000} max={10000000}/>
                    </Form.Item>


                </div>
                <div  style={{display: 'flex'}}>
                    <Form.Item style={{maxWidth: '300px',minWidth: '250px', marginRight: '10px'}} name={'srcPrice'} label="Src Price">
                        <Slider range
                                tipFormatter={(value) => <NumberField style={{color: 'white'}} value={value}/>}
                                min={0} max={50000000}/>
                    </Form.Item>
                    <Form.Item style={{maxWidth: '300px',minWidth: '250px', marginRight: '10px'}} name={'dstPrice'} label="Digi Price">
                        <Slider range
                                tipFormatter={(value) => <NumberField style={{color: 'white'}} value={value}/>}
                                min={0} max={50000000}/>
                    </Form.Item>
                </div>
            </div>
            <Form.Item>
                <Button type="primary" htmlType="submit">
                    Search
                </Button>
                <Button style={{marginLeft: '10px'}} type="primary" onClick={saveSearch}>
                    Save Search
                </Button>

            </Form.Item>
        </Form>
        <Modal
            title="Save Search"
            visible={isModalVisible}
            onOk={handleOk}
            onCancel={handleCancel}
        >
            <Input
                placeholder="Enter search name"
                value={searchName}
                onChange={(e) => setSearchName(e.target.value)}
            />
            <SearchPreview search={convertFiltersToRequest(filters)}/>
        </Modal>

        <Table
            dataSource={data || []}
            pagination={{
                ...pagination,
                onChange: (current, pageSize) => {
                    setPagination({...pagination, current: current, pageSize: pageSize});
                },
            }}
            onChange={(pagination, _, sorter) => {
                setPagination({current: pagination.current, pageSize: pagination.pageSize});
                setSorter(sorter);
            }}
            columns={[
                {
                    title: "Title",
                    dataIndex: "title",
                    render: (value, record: any) => <div style={{display: 'flex', flexDirection: 'column', minWidth:'200px'}}>
                        <Link to={record.srcLink} target={'_blank'}>{value}</Link>
                        <Link to={getDigikalaLink(record.dstId)} target={'_blank'}>{record.dstTitle}</Link>
                    </div>
                },
                {
                    title: "Src",
                    dataIndex: "src",
                    render: (value) => <Link to={'https://' + value} target={'_blank'} >{value}</Link>
                },
                {title: "Price", dataIndex: "srcPrice", sorter: true, render: (value) => <NumberField value={value}/>},
                {
                    title: "Digi Price",
                    dataIndex: "dstPrice",
                    sorter: true,
                    render: (value) => <NumberField value={value}/>
                },
                {title: "Category", dataIndex: "dstCat"},
                {title: "Brand", dataIndex: "dstBrand"},
                {title: "Profit", dataIndex: "profit", sorter: true, render: (value) => <NumberField value={value}/>},
                {title: "ROI", dataIndex: "roi", sorter: true, render: (value) => <NumberField value={value * 100}/>},
                {
                    title: "Matched Images",
                    dataIndex: "srcImageLink",
                    render: (value, record: any) => <ImagePreview images={[
                        {link: value, title: record.src}, {link: record.dstImageLink, title: 'Digikala'}]}/>
                },
                {
                    title: "Detail",
                    dataIndex: "dstId",
                    render: (value) => <Link to={"/product/" + value}><AreaChartOutlined/></Link>
                },
            ]}

        />
    </div>:<div/>;
};