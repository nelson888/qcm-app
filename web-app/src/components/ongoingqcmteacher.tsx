import React from "react";
import {Qcm, Question} from "../types";
import {QcmClient, QuestionResponse} from "../services/qcmClient";
import {toast} from "react-toastify";
import OnGoingQCM from "./ongoingqcm";

type Props = {
    qcm: Qcm,
    apiClient: QcmClient
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

    private nextQuestion = () => {
        this.setState({loading: true});
        const {qcm, apiClient} = this.props;
        apiClient.nextQuestion(qcm.id)
            .then((response: QuestionResponse) => {
                this.setState({loading: false});
                console.log(response);
                if (response.isSuccess) {
                    this.setState({question: response.successData });
                } else {
                    if (response.code === 404) {
                        toast.error("There is no next question for this qcm");
                    } else {
                        toast.error("An error occurred: " + response.errorData);
                    }
                }
            })
            .catch((error: any) => {
                this.setState({loading: false});
                toast.error("An error occurred: " + error.toString());
            });
    }
}

export default OnGoingQCMTeacher;
