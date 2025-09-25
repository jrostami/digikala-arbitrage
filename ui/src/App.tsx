import {Authenticated, Refine} from "@refinedev/core";
import {RefineKbar, RefineKbarProvider} from "@refinedev/kbar";

import {
    AuthPage,
    ErrorComponent,
    notificationProvider,
    ThemedLayoutV2,
    ThemedSiderV2,
    ThemedTitleV2,
} from "@refinedev/antd";
import "@refinedev/antd/dist/reset.css";

import routerBindings, {DocumentTitleHandler, UnsavedChangesNotifier,} from "@refinedev/react-router-v6";
import {dataProvider} from "./providers/providers";
import {useTranslation} from "react-i18next";
import {BrowserRouter, Outlet, Route, Routes} from "react-router-dom";
import {AppIcon} from "./components/app-icon";
import {Header} from "./components";
import {ColorModeContextProvider} from "./contexts/color-mode";
import {MatchList,} from "./pages/match-list";
import {SearchProduct} from "./pages/search-products/SearchProduct";
import {ProductShow} from "./pages/search-products/show";
import {authProvider} from "./providers/AuthProvider";
import {SaveSearchList} from "./pages/save-search";
import {BookOutlined, SearchOutlined} from "@ant-design/icons";
import {SiteList} from "./pages/crawled-sites-list/list";
import {CrawlSiteCreate} from "./pages/crawled-sites-list/create";

export const dataProvide = dataProvider("/api");

function App() {
    const {t, i18n} = useTranslation();

    const i18nProvider = {
        translate: (key: string, params: object) => t(key, params),
        changeLocale: (lang: string) => i18n.changeLanguage(lang),
        getLocale: () => i18n.language,
    };


    return (
        <BrowserRouter>
            <RefineKbarProvider>
                <ColorModeContextProvider>
                    <Refine
                        dataProvider={{default: dataProvide}}
                        authProvider={authProvider}
                        notificationProvider={notificationProvider}
                        i18nProvider={i18nProvider}
                        routerProvider={routerBindings}
                        resources={[
                            {
                                name: "product",
                                list: "/",
                                show: "/product/:id",
                                meta:{
                                    icon: <SearchOutlined/>
                                }
                            },
                            {
                                name: "user/saveSearch",
                                list: "/user/savedSearch",
                                meta: {
                                    label: "Saved Search",
                                    method: "get",
                                    icon: <BookOutlined />
                                }
                            },
                            {
                                name: "crawl",
                                list: "/crawl",
                                create: '/crawl/create',
                                meta: {
                                    label: "Site",
                                    method: "get"
                                }
                            },
                            {
                                name: "matched",
                                list: "/matched/search",
                                meta: {
                                    label: "Match",
                                    method: "post"
                                }
                            }
                        ]}
                        options={{
                            syncWithLocation: true,
                            warnWhenUnsavedChanges: true,
                        }}
                    >
                        <Routes>
                            <Route path="/login" element={<AuthPage title={'Digi Arbitrage'} type="login"/>}/>
                            <Route path="/register" element={<AuthPage title={'Digi Arbitrage'} type="register"/>}/>
                            <Route
                                element={
                                    <Authenticated>
                                        <ThemedLayoutV2
                                            Header={() => <Header sticky/>}
                                            Sider={(props) => <ThemedSiderV2 {...props} fixed/>}
                                            Title={({collapsed}) => (
                                                <ThemedTitleV2
                                                    collapsed={collapsed}
                                                    text="Digi Arbitrage"
                                                    icon={<AppIcon/>}
                                                />
                                            )}
                                        >
                                            <Outlet/>
                                        </ThemedLayoutV2>
                                    </Authenticated>
                                }
                            >

                                <Route path="/">
                                    <Route path={''} element={
                                        <SearchProduct/>}/>
                                    <Route path="product/:id" element={
                                        <ProductShow/>
                                    }/>
                                </Route>
                                <Route path="/">
                                    <Route index path={'user/savedSearch'} element={<SaveSearchList/>}/>
                                </Route>
                                <Route path="/matched">
                                    <Route index path={'search'} element={<MatchList/>}/>
                                </Route>
                                <Route path="/crawl">
                                    <Route index path={''} element={<SiteList/>}/>
                                    <Route index path={'create'} element={<CrawlSiteCreate/>}/>
                                </Route>
                                <Route path="*" element={<ErrorComponent/>}/>
                            </Route>
                        </Routes>

                        <RefineKbar/>
                        <UnsavedChangesNotifier/>
                        <DocumentTitleHandler/>
                    </Refine>
                </ColorModeContextProvider>
            </RefineKbarProvider>
        </BrowserRouter>
    );
}

export default App;
