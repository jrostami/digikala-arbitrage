import {IResourceComponentsProps, useResource} from "@refinedev/core";
import {DateField, List, NumberField, useTable,} from "@refinedev/antd";
import {Image, Radio, Table} from "antd";
import {dataProvide} from "../../App";
import {Link} from "react-router-dom";
export const DigikalaPrefixLink = 'https://www.digikala.com/product/dkp-';

export function getDigikalaLink(id: string){
    return DigikalaPrefixLink + id;
}
export const StatusOptions = ["Rejected", "Pending", "Confirmed"].map(item => {
    return {label: item, value: item}
});
export const MatchList: React.FC<IResourceComponentsProps> = () => {
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
                <Table.Column dataIndex="title" title="Src Title"
                    render={(value:string, object:any) => (
                        <Link to={object.srcLink} target={'_blank'}>{value}</Link>
                    )}

                />
                <Table.Column dataIndex="dstTitle" title="Digi Title"
                    render={(value:string, object:any) => (
                        <Link to={getDigikalaLink(object.dstId)} target={'_blank'}>{value}</Link>)}/>

                <Table.Column sorter dataIndex="srcPrice" title="Price" render={(value)=>(<NumberField value={value}/>)}/>
                <Table.Column sorter dataIndex="dstPrice" title="Digi Price" render={(value)=>(<NumberField value={value}/>)}/>
                <Table.Column sorter dataIndex="profit" title="Profit" render={(value)=>(<NumberField value={value}/>)}/>
                <Table.Column sorter dataIndex="roi" title="ROI" render={(value)=>(<NumberField value={value * 100}/>)}/>

                <Table.Column dataIndex="imageSimilarity" title="Similarity"
                              render={(value, object: any)=>(
                                  <div >
                                  <div>Img: <NumberField
                                      value={value}
                                      options={{
                                          notation: "compact",
                                      }}
                                  /></div>
                                  <div>Text: <NumberField
                                  value={object.titleSimilarity}
                                  options={{
                                  notation: "compact",
                              }}
                                  /></div>
                                  </div>
                              )}
                />
                <Table.Column dataIndex={'srcImageLink'} title={'Image'} render={(value, object:any)=>
                    <Image.PreviewGroup>
                        <div style={{display: 'flex', height: '66px'}}>
                            <Image style={{maxWidth: '100px', height: '100%'}} src={value}/>
                            <Image style={{maxWidth: '100px', height: '100%'}} src={object.dstImageLink}/>
                        </div>
                    </Image.PreviewGroup>
                } />

                <Table.Column dataIndex="status" title="Status" render={(value, object:any)=>
                    <Radio.Group style={{maxWidth: '120px', fontSize:'10px'}}
                        options={StatusOptions} onChange={(value) => setStatus(object.id, value)} value={value} />
                }/>

            </Table>
        </List>
    );
};

interface IPost {
    id: number;
    title: string;
    createdAt: string;
}