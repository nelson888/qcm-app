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
    question: Question|null,
    choices: number[]
};


class OngoingQCMStudent extends OnGoingQCM<Props, State> {

    state: State = {
        loading: true,
        question: null,
        choices: []
    };
    loadingMessage = "Waiting for next question...";


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
        this.setState({loading: true, choices: [] });
        //TODO api call
    }
}

export default OngoingQCMStudent;
