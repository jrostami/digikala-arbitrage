import axios from "axios";
import type { AuthProvider } from "@refinedev/core";

export const authProvider:AuthProvider = {
    login: async ({email, password }) => {
        const user = {email, password};
        try {
            const response = await axios.post(
                '/api/user/login',
                user,
                { withCredentials: true } // Include credentials (JSESSIONID)
            );
            if (response.status === 200) {
                return {
                    success: true,
                    redirectTo: "/",
                };
            }
            return {
                success: false,
                error: {
                    message: "Login Error",
                    name: "Invalid email or password",
                },
            };
        } catch (error) {
            return Promise.reject(error);
        }
    },
    logout: async () => {
        try {
            await axios.post("/api/logout", {}, {withCredentials: true});

        }catch (e){

        }
        return {
            success: true,
            redirectTo: "/login",
        };
    },
    check: async () => {
        try{
        const user = await axios.get("/api/user/me", { withCredentials: true });

        if (user) {
            return {
                authenticated: true,
            };
        }
        }catch (e) {

            return {
                authenticated: false,
                logout: true,
                redirectTo: "/login",
                error: {
                    message: "Check failed",
                    name: "Unauthorized",
                },
            };
        }
    },
    checkAuth: async () => {
        try {
            await axios.get("/api/user/me", { withCredentials: true });
            return Promise.resolve();
        } catch (error) {
            return Promise.reject();
        }
    },
    checkError: (error) => {
        if (error.response?.status === 401) {
            return Promise.reject();
        }
        return Promise.resolve();
    },
    getPermissions: async () => {
        const response = await axios.get("/api/user/me", { withCredentials: true });
        return Promise.resolve(response.data.roles);
    },
    getIdentity: async () => {
        const response = await axios.get("/api/user/me", { withCredentials: true });
        return Promise.resolve(response.data);
    },
};
