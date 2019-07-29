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

    private post(url: string, data: any): Promise<Response> {
        return fetch(url,
            {
                //   credentials: 'same-origin', // MANDATORY TO SOLVE CORS PROBLEM, ALSO DON'T ADD HEADER CONTENT-TYPE JSON!!! IT WILL MAKE CORS A PAIN IN THE *SS
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(data)
            });
    }
    logIn = async (username: string, password: string): Promise<LoginResponse> =>  {
        let response: Response = await this.post(API_URL + '/auth/login',
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
    }
}

export interface QcmClient {
    isLogged(): boolean,
    logIn(username: string, password: string): Promise<LoginResponse>
    getRole(): Role
}

export { ApiClient };
