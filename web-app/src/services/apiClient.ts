import {Qcm} from "../types";

const API_URL = 'http://localhost:8080';

export type Role = 'ROLE_TEACHER' | 'ROLE_STUDENT';
export const TEACHER: Role = 'ROLE_TEACHER';
export const STUDENT: Role = 'ROLE_STUDENT';

type login_response = {
    jwt: string,
    role: Role,
    username: string
};
type String = string|null;
type User = login_response;

export type LoginResponse = APIResponse<User, string>;
export type QcmAllResponse = APIResponse<Qcm[], string>;

type APIResponseConstructor<S, E> = {
    isSuccess: boolean,
    successData?: S,
    errorData?: E
}
class APIResponse<S, E> {

    public readonly isSuccess: boolean;
    private _successData?: S;
    private _errorData?: E;

    constructor({isSuccess, successData, errorData}: APIResponseConstructor<S, E>) {
        this.isSuccess = isSuccess;
        this._errorData = errorData;
        this._successData = successData;
    }

    get errorData(): E {
        return this._errorData as E;
    }

    get successData(): S {
        return this._successData as S;
    }
}

class ApiClient implements QcmClient {

    private user: User = {
        username: "",
        role: STUDENT,
        jwt: ""
    };

    private getHeaders = (): any => {
        let headers: any = {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        };
        if (this.isLogged()) {
            headers['Authorization'] = `Bearer ${this.user.jwt}`;
        }
        return headers;
    };

    private post(endpoint: string, data: any): Promise<Response> {
        return fetch(API_URL + endpoint,
            {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify(data)
            });
    }

    private get(endpoint: string): Promise<Response> {
        return fetch(API_URL + endpoint,
            {
                method: 'GET',
                headers: this.getHeaders(),
            });
    }

    private async extractErrorMessage(response: Response): Promise<string> {
        let errorResponse: any =  await response.json();
        return errorResponse.message;
    }

    private isError(response: Response): boolean {
        return response.status < 200 || response.status >= 300;
    }

    private async errorResponse<T>(response: Response): Promise<APIResponse<T, string>> {
        return new APIResponse({
            isSuccess: false,
            errorData: await this.extractErrorMessage(response)
        });
    }
    logIn = async (username: string, password: string): Promise<LoginResponse> =>  {
        let response: Response = await this.post('/auth/login',
            {
                username,
                password
            });

        if (this.isError(response)) {
            return this.errorResponse<User>(response);
        }
        let json: login_response = await response.json();
        console.log(json);
        this.user = {...json};

        return new APIResponse({
            isSuccess: true,
            successData: {...this.user}
        });
    };

    isLogged = (): boolean => {
        return !!this.user.jwt;
    };

    getRole = (): Role => {
        return this.user.role;
    };

    async getQcms(): Promise<QcmAllResponse> {
        let response: Response = await this.get('/qcm/all');
        if (this.isError(response)) {
            return await this.errorResponse<Qcm[]>(response);
        }
        let jsonList: Qcm[] = await response.json();
        return new APIResponse({
            isSuccess: true,
            successData: jsonList
        });
    }

}

export interface QcmClient {
    isLogged(): boolean,
    logIn(username: string, password: string): Promise<LoginResponse>
    getRole(): Role,
    getQcms(): Promise<QcmAllResponse>
}

export { ApiClient, APIResponse };
