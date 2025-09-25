import {BaseRecord, CrudSort, useNotification} from "@refinedev/core";
import {Image, Input, Table} from "antd";
import React, {useEffect, useState} from "react";
import {NumberField} from "@refinedev/antd";
import {dataProvide} from "../../App";
import {Link, useSearchParams} from "react-router-dom";

import {ImagePreview} from "./SearchProduct";

const preDefaultFilters = {status: 'Confirmed'}
export const SearchShowProduct = ({defaultFilters}: SearchProductProps) => {
    const [data, setData] = useState<BaseRecord>([]);
    const [pagination, setPagination] = useState<{current: any, pageSize: any, total: any}>({current: 1, pageSize: 10, total: 0});
    const [total, setTotal] = useState();
    const [filters, setFilters] = useState({});
    const {open} = useNotification();
    const [sorter, setSorter] = useState<CrudSort>({
        field: 'created',
        order: 'desc',
    });

    const [loading, setLoading] = useState(false);
    const [searchParams, setSearchParams] = useSearchParams();

    // Sync state with URL on mount
    useEffect(() => {
        const page = searchParams.get("page") || "1";
        const pageSize = searchParams.get("pageSize") || "10";
        const filtersFromUrl = searchParams.get("filters");
        const sorterFromUrl = searchParams.get("sorter");

        setPagination({ ...pagination, current: parseInt(page), pageSize: parseInt(pageSize) });

        if (filtersFromUrl) {
            setFilters(JSON.parse(filtersFromUrl));
        }
        if (sorterFromUrl) {
            setSorter(JSON.parse(sorterFromUrl));
        }
    }, []);

    // Sync URL with state
    useEffect(() => {

        setSearchParams({
            page: pagination.current,
            pageSize: pagination.pageSize,
            filters: JSON.stringify(filters),
            sorter: JSON.stringify(sorter),
        });
    }, [pagination, filters, sorter]);

    useEffect(() => {
        // Fetch data based on the current pagination, sorter, and filters
        search(filters);
    }, [pagination, sorter, filters, defaultFilters]);

    function search(values: any) {
        const f = defaultFilters?defaultFilters:preDefaultFilters;
        setLoading(true);
        dataProvide.getList({
            resource: "matched",
            filters: {...values, ...f},
            sorters: [sorter],
            pagination: pagination
        }).then(data => {
            setData(data.data);
            if(pagination.total!== data?.total)
                setTotal(data?.total);
            setLoading(false);
        }).catch(() => {
            open?.({
                type: "error",
                message: "Load Error",
                description: "Try again"
            })
            setLoading(false);
        })
    }


    return <div>
        <Table
            loading={loading}
            dataSource={data || []}
            pagination={{
                ...pagination, total,
                onChange: (current, pageSize) => {
                    setPagination({...pagination, current: current, pageSize: pageSize});
                },
            }}
            onChange={(pagination, _, sorter) => {
                setPagination({ current: pagination.current, pageSize: pagination.pageSize });
                setSorter(sorter);
            }}
            columns={[
                {title: "Title", dataIndex: "title", render: (value, record:any)=><Link to={record.srcLink } target={'_blank'}>{value}</Link>},
                {title: "Src", dataIndex: "src"},
                {title: "Price", dataIndex: "srcPrice", sorter: true, render: (value) => <NumberField value={value}/>},
                {title: "Commission", dataIndex: "digiCommission", sorter: true, render: (value) => <NumberField value={value}/>},
                {title: "Profit", dataIndex: "profit", sorter: true, render: (value) => <NumberField value={value}/>},
                {title: "ROI", dataIndex: "roi", sorter: true, render: (value) => <NumberField value={value * 100}/>},
                {title: "Matched Images", dataIndex:"srcImageLink", render: (value, record:any)=><ImagePreview images={[
                    {link: value, title: record.src}, {link: record.dstImageLink, title: 'Digikala'}]}/>}
            ]}

        />
    </div>;
};

interface SearchProductProps{
    defaultFilters?: any,
}