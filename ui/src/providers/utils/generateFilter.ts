import { CrudFilters } from "@refinedev/core";
import { mapOperator } from "./mapOperator";

export const generateFilter = (filters?: CrudFilters) => {
    const queryFilters: { [key: string]: string } = {};

    if (filters) {
        filters.map((filter) => {
            if ("field" in filter) {
                const { field, value } = filter;
                queryFilters[field] = value;
            }
        });
    }

    return queryFilters;
};
