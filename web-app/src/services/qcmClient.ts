import {Qcm, QcmResult, Question} from "../types";

export type Role = 'TEACHER' | 'STUDENT';
export const TEACHER: Role = 'TEACHER';
export const STUDENT: Role = 'STUDENT';

export type User = {
    role: Role,
    username: string
};
export type login_response = User & {
    expires: string,
    jwt: string,
};
export type LoginResponse = APIResponse<login_response, string>;
export type QcmAllResponse = APIResponse<Qcm[], string>;
export type MeResponse = APIResponse<User, string>;
export type QcmResponse = APIResponse<Qcm, string>;
export type VoidResponse = APIResponse<{}, string>;
export type QuestionResponse = APIResponse<Question, string>;
export type ResultResponse = APIResponse<QcmResult, string>;


type APIResponseConstructor<S, E> = {
    isSuccess: boolean,
    code: number,
    successData?: S,
    errorData?: E
}
class APIResponse<S, E> {

    public readonly isSuccess: boolean;
    public readonly code: number;
    private _successData?: S;
    private _errorData?: E;

    constructor({isSuccess, successData, errorData, code}: APIResponseConstructor<S, E>) {
        this.isSuccess = isSuccess;
        this.code = code;
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

export interface QcmClient {
    isLogged(): boolean,
    logIn(username: string, password: string): Promise<LoginResponse>
    getRole(): Role,
    getMyQcms(): Promise<QcmAllResponse>,
    getQcms(): Promise<QcmAllResponse>,
    newQcm(): Promise<QcmResponse>,
    updateQcm(qcm: Qcm): Promise<QcmResponse>,
    deleteQcm(id: number): Promise<QcmResponse>,
    getMe(): Promise<MeResponse>,
    launchQcm(id: number): Promise<VoidResponse>,
    finishQcm(id: number): Promise<VoidResponse>,
    nextQuestion(id: number): Promise<QuestionResponse>,
    currentQuestion(qcmId: number): Promise<QuestionResponse>,
    setUser(user: User): void
    setJwt(jwt: string): void,
    getResult(id: number): Promise<ResultResponse>
    postChoices(ids: number[]): Promise<VoidResponse>
}

export { APIResponse };
