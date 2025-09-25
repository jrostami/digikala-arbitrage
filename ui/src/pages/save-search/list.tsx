import {IResourceComponentsProps, useResource} from "@refinedev/core";
import {DateField, List, NumberField, useTable,} from "@refinedev/antd";
import {Image, Radio, Table} from "antd";
import {dataProvide} from "../../App";
import {Link} from "react-router-dom";
import {convertFiltersToUrlParam, SearchPreview} from "../search-products/SearchProduct";
export const DigikalaPrefixLink = 'https://www.digikala.com/product/dkp-';

export function getDigikalaLink(id: string){
    return DigikalaPrefixLink + id;
}
export const StatusOptions = ["Rejected", "Pending", "Confirmed"].map(item => {
    return {label: item, value: item}
});
export const SaveSearchList: React.FC<IResourceComponentsProps> = () => {
    const {tableProps} = useTable<IPost>({
        sorters: {
            initial: [
                {
                    field: "created",
                    order: "desc",
                },
            ],
        }
    });

    function setStatus(id: string, e: any) {

        dataProvide.create({resource: "matched/" + id + "/status?status=" + e.target.value, variables: null})
            .then().catch()
    }

    return (
        <List >
            <Table {...tableProps} rowKey="id">
                <Table.Column dataIndex="name" title="Name" />
                <Table.Column dataIndex="search" title="Search"  render={(value)=><SearchPreview search={value}/>}/>
                <Table.Column dataIndex="search" title="Action"  render={(value)=>{
                    return <Link to={'/?filters=' + JSON.stringify(convertFiltersToUrlParam(value))}>Search</Link>
                }}/>
            </Table>
        </List>
    );
};

interface IPost {
    id: number;
    title: string;
    createdAt: string;
}