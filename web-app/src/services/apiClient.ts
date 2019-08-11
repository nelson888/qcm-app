import {Qcm, QcmResult, Question} from "../types";
import {
    APIResponse, BoolResponse,
    login_response,
    LoginResponse, MeResponse, NullableQuestionResponse, QcmAllResponse,
    QcmClient, QcmResponse, QuestionResponse, ResultResponse, Role,
    STUDENT,
    User, VoidResponse
} from "./qcmClient";

const API_URL = 'http://localhost:8080';

class ApiClient implements QcmClient {

    private user: User = {
        username: "",
        role: STUDENT,
    };
    private jwt: string = "";

    private getHeaders = (): any => {
        let headers: any = {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        };
        if (this.isLogged()) {
            headers['Authorization'] = `Bearer ${this.jwt}`;
        }
        return headers;
    };

    private dataRequest(method: string, endpoint: string, data: any): Promise<Response> {
        return fetch(API_URL + endpoint,
            {
                method,
                headers: this.getHeaders(),
                body: JSON.stringify(data)
            });
    }

    private post(endpoint: string, data: any): Promise<Response> {
        return this.dataRequest('POST', endpoint, data);
    }

    private put(endpoint: string, data: any): Promise<Response> {
        return this.dataRequest('PUT', endpoint, data);
    }

    private get(endpoint: string): Promise<Response> {
        return fetch(API_URL + endpoint,
            {
                method: 'GET',
                headers: this.getHeaders(),
            });
    }

    private delete(endpoint: string): Promise<Response> {
        return fetch(API_URL + endpoint,
            {
                method: 'DELETE',
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
            errorData: await this.extractErrorMessage(response),
            code: response.status
        });
    }
    logIn = async (username: string, password: string): Promise<LoginResponse> =>  {
        let response: Response = await this.post('/auth/login',
            {
                username,
                password
            });

        if (this.isError(response)) {
            return this.errorResponse<login_response>(response);
        }
        let json: login_response = await response.json();
        this.jwt = json.jwt;
        this.user = {...json};

        return new APIResponse({
            isSuccess: true,
            successData: {...json},
            code: response.status
        });
    };

    isLogged = (): boolean => {
        return !!this.jwt;
    };

    getRole = (): Role => {
        return this.user.role;
    };

    async getMyQcms(): Promise<QcmAllResponse> {
        let response: Response = await this.get('/qcm/mines');
        if (this.isError(response)) {
            return await this.errorResponse<Qcm[]>(response);
        }
        let jsonList: Qcm[] = await response.json();
        return new APIResponse({
            isSuccess: true,
            successData: jsonList,
            code: response.status
        });
    }

    async getQcms(): Promise<QcmAllResponse> {
        let response: Response = await this.get('/qcm/all');
        if (this.isError(response)) {
            return await this.errorResponse<Qcm[]>(response);
        }
        let jsonList: Qcm[] = await response.json();
        return new APIResponse({
            isSuccess: true,
            successData: jsonList,
            code: response.status
        });
    }

    async getMe(): Promise<MeResponse> {
        let response: Response = await this.get('/users/me');
        if (this.isError(response)) {
            return await this.errorResponse<User>(response);
        }
        let json: User = await response.json();
        this.user = {...json};
        return new APIResponse({
            isSuccess: true,
            successData: {...json},
            code: response.status
        });
    }

    async newQcm(): Promise<QcmResponse> {
        let response: Response = await this.get('/qcm/new');
        if (this.isError(response)) {
            return await this.errorResponse<Qcm>(response);
        }
        let json: Qcm = await response.json();
        return new APIResponse({
            isSuccess: true,
            successData: json,
            code: response.status
        });
    }

    async launchQcm(id: number): Promise<VoidResponse> {
        let response: Response = await this.get(`/qcm/${id}/launch`);
        if (this.isError(response)) {
            return await this.errorResponse<{}>(response);
        }
        return new APIResponse({
            isSuccess: true,
            successData: {},
            code: response.status
        });
    }

    async getResult(id: number): Promise<ResultResponse> {
        let response: Response = await this.get(`/qcm/${id}/result`);
        if (this.isError(response)) {
            return await this.errorResponse<QcmResult>(response);
        }
        let json: QcmResult = await response.json();
        return new APIResponse({
            isSuccess: true,
            successData: json,
            code: response.status
        });
    }


    async finishQcm(id: number): Promise<VoidResponse> {
        let response: Response = await this.get(`/qcm/${id}/finish`);
        if (this.isError(response)) {
            return await this.errorResponse<{}>(response);
        }
        return new APIResponse({
            isSuccess: true,
            successData: {},
            code: response.status
        });
    }

    async nextQuestion(id: number): Promise<NullableQuestionResponse> {
        let response: Response = await this.get(`/qcm/${id}/nextQuestion`);
        if (this.isError(response)) {
            return await this.errorResponse<Question>(response);
        }
        let output: string = await response.text();
        let question: Question|null = null;
        if (output) {
            question = JSON.parse(output);
        }
        return new APIResponse({
            isSuccess: true,
            successData: question,
            code: response.status
        });
    }

    async currentQuestion(qcmId: number): Promise<QuestionResponse> {
        let response: Response = await this.get(`/qcm/${qcmId}/currentQuestion`);
        if (this.isError(response)) {
            return await this.errorResponse<Question>(response);
        }
        let json: Question = await response.json();
        return new APIResponse({
            isSuccess: true,
            successData: json,
            code: response.status
        });
    }

    async hasAnswered(question: Question): Promise<BoolResponse> {
        let response: Response = await this.get(`/question/${question.id}/hasAnswered`);
        if (this.isError(response)) {
            return await this.errorResponse<boolean>(response);
        }
        let json: boolean = await response.json();
        return new APIResponse({
            isSuccess: true,
            successData: json,
            code: response.status
        });
    }

    async updateQcm(qcm: Qcm): Promise<QcmResponse> {
        let response: Response = await this.put(`/qcm/${qcm.id}`, qcm);
        if (this.isError(response)) {
            return await this.errorResponse<Qcm>(response);
        }
        let json: Qcm = await response.json();
        return new APIResponse({
            isSuccess: true,
            successData: json,
            code: response.status
        });
    }

    async deleteQcm(id: number): Promise<QcmResponse> {
        let response: Response = await this.delete(`/qcm/${id}`);
        if (this.isError(response)) {
            return await this.errorResponse<Qcm>(response);
        }
        let json: Qcm = await response.json();
        return new APIResponse({
            isSuccess: true,
            successData: json,
            code: response.status
        });
    }

    setUser(user: User): void {
        this.user = user;
    }

    setJwt(jwt: string): void {
        this.jwt = jwt;
    }

    logOut(): void {
        this.jwt = "";
        this.user = {
          username: "",
          role: "STUDENT"
        };
    }

    postChoices = async (ids: number[]): Promise<VoidResponse> => {
        let response: Response = await this.post('/response/',
            {
                ids
            });
        if (this.isError(response)) {
            return await this.errorResponse<{}>(response);
        }
        return new APIResponse({
            isSuccess: true,
            code: response.status
        });
    }
}

export default ApiClient;
