import React from "react";
import {Choice, Qcm, Question} from "../types";
import {NullableQuestionResponse, QcmClient} from "../services/qcmClient";
import {toast} from "react-toastify";
import OnGoingQCM from "./ongoingqcm";
import './ongoingqcmteacher.scss';

type Props = {
    qcm: Qcm,
    apiClient: QcmClient,
    refresh(): void
};

type State = {
    loading: boolean,
    question: Question|null
};

class OnGoingQCMTeacher extends OnGoingQCM<Props, State> {

    state: State = {
        loading: true,
        question: null
    };
    loadingMessage = "Loading next question...";

    renderContent(q: Question, qcm: Qcm, index: number, isLast: boolean): React.ReactElement {
        return (
        <React.Fragment>
            <p
                className='unselectable'
            >Question {index + 1} out of {qcm.questions.length}</p>

            {this.renderQuestion(q)}
            <button
                style={{
                    float: 'right',
                    margin: 20
                }}
                onClick={this.nextQuestion}
            >
                {isLast ? "Finish MCQ" : "Next Question"}
            </button>
        </React.Fragment>
        );
    }

    private nextQuestion = async () => {
        this.setState({loading: true});
        const {qcm, apiClient} = this.props;
        const response: NullableQuestionResponse = await apiClient.nextQuestion(qcm.id);
        console.log(response);
        if (response.isSuccess) {
            if (response.successData != null) {
                this.setState({question: response.successData, loading: false });
            } else {
                this.props.refresh();
            }
        } else {
            this.setState({loading: false});
            if (response.code === 404) {
                toast.error("There is no next question for this qcm");
            } else {
                toast.error("An error occurred: " + response.errorData);
            }
        }
    };

    protected choiceClassName = (c: Choice): string => {
        return c.answer ? "teacher-choice " : "teacher-answer";
    };
}

export default OnGoingQCMTeacher;
