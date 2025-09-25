import {DateField, NumberField, Show} from "@refinedev/antd";
import {Typography} from "antd";
import {useResource, useShow} from "@refinedev/core";
import {ImagePreview} from "./SearchProduct";
import {getDigikalaLink} from "../match-list/list";
import {Link} from "react-router-dom";
import {SearchShowProduct} from "./SearchShowProduct";

const {Title} = Typography;

export function getImages(images:string[] | undefined){
    if(images && images.length> 0){
        return images.map(link=>{
            return {link: link, title: ''}
        });
    }
    return [];
}

export const ProductShow: React.FC = () => {
    const {queryResult} = useShow<IPost>();

    const {data, isLoading} = queryResult;
    const record = data?.data;
    const {id} = useResource("product/");



    return <div>
        <Show isLoading={isLoading}>
            <div style={{display: 'flex'}}>
                <div><ImagePreview width={400} style={{}} images={getImages(record?.images)}/></div>
                <div style={{padding: '50px 10px'}}>
                    <Title level={4}><Link to={getDigikalaLink(record?.id)} target={'_blank'}>{record?.title}</Link></Title>
                    <Title level={5}>Price: <NumberField value={record?.price}/></Title>
                    <Title level={5}>Category: {record?.cat}</Title>
                    <Title level={5}>Brand: {record?.brand}</Title>
                </div>

            </div>
        </Show>
        <SearchShowProduct defaultFilters={{digiId:  record?.id, status:'Confirmed'}}/>
    </div>;
};

interface IPost {
    id: number;
    title: string;
    status: string;
    link: string;
    price: number;
    images: string[]
}
