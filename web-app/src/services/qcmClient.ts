import {Qcm} from "../types";


export type Role = 'ROLE_TEACHER' | 'ROLE_STUDENT';
export const TEACHER: Role = 'ROLE_TEACHER';
export const STUDENT: Role = 'ROLE_STUDENT';

export type login_response = {
    jwt: string,
    role: Role,
    username: string
};
export type String = string|null;
export type User = login_response;

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

export interface QcmClient {
    isLogged(): boolean,
    logIn(username: string, password: string): Promise<LoginResponse>
    getRole(): Role,
    getQcms(): Promise<QcmAllResponse>
}

export { APIResponse };
