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
type APIResponse<S, E> = {
    isSuccess: boolean,
    successData?: S,
    errorData?: E
}

type User = login_response;

export type LoginResponse = APIResponse<User, Role>;
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

    logIn = async (username: string, password: string): Promise<LoginResponse> =>  {
        let response: Response = await this.post('/auth/login',
            {
                username,
                password
            });

        if (!(response.status >= 200 && response.status < 300)) {
            let errorResponse: any =  await response.json();
            return {
                isSuccess: false,
                errorData: errorResponse.message
            }
        }
        let json: login_response = await response.json();
        console.log(json);
        this.user = {...json};

        return {
            isSuccess: true,
            successData: this.user
        };
    };

    isLogged = (): boolean => {
        return !!this.user.jwt;
    };

    getRole = (): Role => {
        return this.user.role;
    };

    async getQcms(): Promise<any[]> {
        let response: Response = await this.get('/qcm/all');
        let json = await response.json();
        console.log(json);
        return  [];
    }

}

export interface QcmClient {
    isLogged(): boolean,
    logIn(username: string, password: string): Promise<LoginResponse>
    getRole(): Role,
    getQcms(): Promise<any[]>
}

export { ApiClient };
