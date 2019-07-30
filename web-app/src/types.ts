
export type Choice = {
    id: number,
    answer: boolean,
    value: string
}
export type Question = {
    id: number,
    choices: Question[],
    question: string
};

export type QcmState = 'COMPLETE' | 'INCOMPLETE' | 'STARTED' | 'FINISHED';

export type Qcm = {
    id: number,
    name: string,
    state: QcmState
};
