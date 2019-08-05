import React from "react";
import {Choice, Qcm, Question} from "../types";
import {QcmClient} from "../services/qcmClient";
import OnGoingQCM from "./ongoingqcm";

type Props = {
    qcm: Qcm,
    apiClient: QcmClient
};

type State = {
    loading: boolean,
    question: Question|null
};


class OngoingQCMStudent extends OnGoingQCM<Props, State> {

    state: State = {
        loading: true,
        question: null
    };
    loadingMessage = "Waiting for next question...";


    renderContent(q: Question, qcm: Qcm, index: number, isLast: boolean): React.ReactElement {
        return this.renderQuestion(q);
    }

    protected onChoiceClick(c: Choice) {
        this.setState({loading: true});
        //TODO
    }
}

export default OngoingQCMStudent;
