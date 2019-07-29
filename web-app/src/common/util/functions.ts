import {History} from "history";

export const isEmpty = (o: object): boolean => {
    for(let key in o) {
        if (o.hasOwnProperty(key)) {
            return false;
        }
    }
    return true;
};

export const getUrlParams = () : any => {
    const searchParams: URLSearchParams = new URLSearchParams(window.location.search);
    let params: any = {};
    searchParams.forEach((value: string, key: string, parent: URLSearchParams) => params[key] = value);
    return params;
};

type LoggedProps = {
    logged: boolean,
    history: History
};

export const firstCharUpper = (s:string): string => {
    return s.charAt(0).toUpperCase() + s.substr(1);
}
