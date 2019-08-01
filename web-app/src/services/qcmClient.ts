import {Qcm} from "../types";

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
    getQcms(): Promise<QcmAllResponse>,
    getMe(): Promise<MeResponse>,
    setUser(user: User): void
    setJwt(jwt: string): void
}

export { APIResponse };
