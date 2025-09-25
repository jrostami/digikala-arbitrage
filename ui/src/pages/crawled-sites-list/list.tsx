import {IResourceComponentsProps} from "@refinedev/core";
import {List, NumberField, useTable,} from "@refinedev/antd";
import {Button, Table} from "antd";
import {dataProvide} from "../../App";
import {Link} from "react-router-dom";
import React from "react";

function PatternPreview({pattern}:any){
    return <div>
        <div><span style={{color:'gray'}}>List</span> {pattern.product_list}</div>
        <div><span style={{color:'gray'}}>Title</span> {pattern.title}</div>
        <div><span style={{color:'gray'}}>Price</span> {pattern.price}</div>
        <div><span style={{color:'gray'}}>Discount</span> {pattern.discount}</div>
        <div><span style={{color:'gray'}}>Link</span> {pattern.link}</div>
        <div><span style={{color:'gray'}}>Image</span> {pattern.main_image}</div>
        <div><span style={{color:'gray'}}>Availability</span> {pattern.availability}</div>
        <div><span style={{color:'gray'}}>Next Page</span> {pattern.next_page}</div>
        <div><span style={{color:'gray'}}>Description</span> {pattern.description}</div>

    </div>
}

export const SiteList: React.FC<IResourceComponentsProps> = () => {
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

    function run(id: string) {
        dataProvide.custom({url: '/api/crawl/' + id + '/run', method: 'get'}).then().catch();
    }

    return (
        <List>
            <Table {...tableProps} rowKey="id">
                <Table.Column dataIndex="shop_url" title="Link"
                              render={(value: string) => (
                                  <Link to={value} target={'_blank'}>{value}</Link>
                              )}

                />
                <Table.Column dataIndex="one_page" title="First Page" render={(value => value ? 'True' : 'False')}/>
                <Table.Column sorter dataIndex="pattern" title="Pattern"
                              render={(value) => <PatternPreview pattern={value} />}/>
                <Table.Column sorter dataIndex="id" title="Action" render={(value) =>
                    <div>
                        <Button onClick={() => run(value)}>Run</Button>
                    </div>
                }/>
            </Table>
        </List>
    );
};

interface IPost {
    id: number;
    shop_url: string;
    pattern: object;
    one_page: boolean;
}