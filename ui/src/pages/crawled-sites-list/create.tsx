import React from "react";
import {Create} from "@refinedev/antd";
import {Button, Checkbox, Form, Input} from "antd";
import {dataProvide} from "../../App";


const patternKeys=[
    'product_list',
    'next_page',
    'link',
    'title',
    'price',
    'discount',
    'main_image',
    'availability',
    'description'
]



export const CrawlSiteCreate = () => {

    function convertValuesToFilters(values){
        const data:any = {pattern: {}};

        Object.keys(values).forEach(key=>{
            if(patternKeys.includes(key))
                data['pattern'][key]=values[key];
            else
                data[key]=values[key];
        });
        return data;
    }

    function onSubmit(values) {
        const data = convertValuesToFilters(values);
        dataProvide.create({resource: 'crawl', variables: data}).then().catch()
    }

    return (
        <Create title="New Crawl" footerButtons={<div></div>}>
            <Form onFinish={onSubmit}>
                <Form.Item style={{minWidth: '200px'}} label={'Src Website'} name={'shop_url'}><Input/></Form.Item>
                <div style={{padding: '0 20px'}}>
                    <div style={{

                        fontSize: '15px',
                        textAlign:'center',
                    }}>Pattern</div>
                    <div style={{display: 'flex', flexWrap: 'wrap'}}>
                        <Form.Item style={{minWidth: '45%', maxWidth: '300px', margin: '10px 10px'}} label={'Product List'}
                                   name={'product_list'}><Input/></Form.Item>
                        <Form.Item style={{minWidth: '45%', maxWidth: '300px', margin: '10px 10px'}} label={'Title'}
                                   name={'title'}><Input/></Form.Item>
                        <Form.Item style={{minWidth: '45%', maxWidth: '300px', margin: '10px 10px'}} label={'Detail Link'}
                                   name={'link'}><Input/></Form.Item>
                        <Form.Item style={{minWidth: '45%', maxWidth: '300px', margin: '10px 10px'}} label={'Price'}
                                   name={'price'}><Input/></Form.Item>
                        <Form.Item style={{minWidth: '45%', maxWidth: '300px', margin: '10px 10px'}} label={'Discount'}
                                   name={'discount'}><Input/></Form.Item>
                        <Form.Item style={{minWidth: '45%', maxWidth: '300px', margin: '10px 10px'}} label={'Image'}
                                   name={'main_image'}><Input/></Form.Item>
                        <Form.Item style={{minWidth: '45%', maxWidth: '300px', margin: '10px 10px'}} label={'Availability'}
                                   name={'availability'}><Input/></Form.Item>
                        <Form.Item style={{minWidth: '45%', maxWidth: '300px', margin: '10px 10px'}} label={'Description'}
                                   name={'description'}><Input/></Form.Item>
                        <Form.Item style={{minWidth: '45%', maxWidth: '300px', margin: '10px 10px'}} label={'Next Page'}
                                   name={'next_page'}><Input/></Form.Item>
                    </div>
                </div>

                <Form.Item valuePropName={'checked'} style={{minWidth: '200px'}} label={'First Page'} name={'one_page'}><Checkbox/></Form.Item>

                <Button type="primary" htmlType="submit">
                    Save
                </Button>
            </Form>
        </Create>
    );
};
