import React from "react";
import {Choice, Qcm, Question} from "../types";
import {QcmClient, QuestionResponse, VoidResponse} from "../services/qcmClient";
import OnGoingQCM from "./ongoingqcm";
import {clearInterval} from "timers";
import {toast} from "react-toastify";

type Props = {
    qcm: Qcm,
    apiClient: QcmClient,
    onRefresh(): void
};

type State = {
    loading: boolean,
    question: Question|null,
    choices: number[],
};


class OngoingQCMStudent extends OnGoingQCM<Props, State> {

    state: State = {
        loading: true,
        question: null,
        choices: []
    };
    loadingMessage = "Waiting for next question...";
    intId: any| null = null;

    componentDidMount(): void {
        super.componentDidMount();
        this.intId = setInterval(this.checkNewQuestion, 800);
    }

    componentWillUnmount(): void {
        if (this.intId != null) {
            clearInterval(this.intId);
        }
    }

    renderContent(q: Question, qcm: Qcm, index: number, isLast: boolean): React.ReactElement {
        return (
            <React.Fragment>
                {this.renderQuestion(q)}

                <button
                    onClick={this.onSubmit}
                >Submit</button>
            </React.Fragment>
        );
    }

    protected onChoiceClick(c: Choice) {
        let choices: number[] = [...this.state.choices];
        if (choices.indexOf(c.id) < 0) {
            choices.push(c.id);
        } else {
            choices = choices.filter(id => id !== c.id);
        }
        this.setState({ choices });
        console.log(choices);
    }

    protected choiceClassName = (c: Choice): string => {
        const {choices} = this.state;
        return "unselectable  " + (choices.indexOf(c.id) >= 0 ? "student-choice-selected" : "student-choice");
    };

    onSubmit = () => {
        const choices: number[] = [...this.state.choices];
        this.loadingMessage = "Posting choices...";
        this.setState({loading: true, choices: [] });
        this.props.apiClient
            .postChoices(choices)
            .then((response: VoidResponse) => {
                if (response.isSuccess) {
                    this.loadingMessage = "Waiting for next question...";
                } else {
                    toast.error(response.errorData);
                    this.props.onRefresh();
                    this.setState({loading: false});
                }
            })
            .catch((error) => {
                toast.error("An error occurred: " + error.toString());
            });
        //TODO api call
    };

    checkNewQuestion = () => {
        const {qcm, apiClient} = this.props;
        apiClient.currentQuestion(qcm.id)
            .then((response: QuestionResponse) => {
                if (response.isSuccess) {
                    const question = response.successData;
                    if (this.state.question !== null && question.id !== this.state.question.id) {
                        this.setState({
                           choices: [],
                            question: question,
                            loading: false
                        });
                    }
                } else {
                    if (response.code === 404) { //not found = no other questions
                        this.props.onRefresh();
                    }
                }
            })
    };
}

export default OngoingQCMStudent;
