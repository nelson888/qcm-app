import React,{Component} from "react";
import LoadingScreen from "../common/components/loadingscreen";
import {Choice, Qcm, Question} from "../types";
import './ongoingqcm.scss';
import {QcmClient, QuestionResponse} from "../services/qcmClient";
import {toast} from "react-toastify";

type Props = {
    qcm: Qcm,
    apiClient: QcmClient
};

type State = {
    loading: boolean,
    question: Question|null
};

class OnGoingQCM extends Component<Props, State> {

    state: State;

    constructor(props: Props) {
        super(props);
        this.state = {
            loading: true,
            question: null
        };
    }

    componentDidMount(): void {
        const {apiClient, qcm} = this.props;

        apiClient.currentQuestion(qcm.id)
            .then((response: QuestionResponse) => {
                this.setState({loading: false});
                if (response.isSuccess) {
                    this.setState({question: response.successData });
                } else {
                    if (response.code === 404) {
                        toast.error("There is no next question for this qcm");
                    } else {
                        toast.error("An error occurred: " + response.errorData);
                    }
                }
            }).catch((error: any) => {
            this.setState({loading: false});
            toast.error("An error occurred: " + error.toString());
        });
    }
//TODO afficher le nombre de reponses?
    render() {
        const {loading, question} = this.state;
        const {qcm} = this.props;

        let index: number = 0;
        let isLast: boolean = false;
        if (question != null) {
            index = qcm.questions.findIndex(q => q.id === question.id);
            isLast = index === qcm.questions.length - 1;
        }
        return (
            <LoadingScreen
                active={loading}
                message={"Loading next question..."}
            >

                {
                    !!question &&
                    <React.Fragment>
                        <p>Question {index + 1} out of {qcm.questions.length}</p>

                        {this.renderQuestion(question)}
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
                }
            </LoadingScreen>
        );
    }

    private renderQuestion = (question: Question): React.ReactElement => {
        return (
            <div
                style={{
                    marginTop: 120,
                    marginBottom: 90
                }}
            >
                <h1
                    className="center-horizontal"
                    style={{
                        textAlign: 'center',
                        margin: 20
                    }}
                >{question.question}</h1>
                <div
                    className="ongoing-choice-view-grid"
                >
                    {question.choices.map(this.renderChoice)}
                </div>
            </div>
        );
    };

    private renderChoice = (c: Choice): React.ReactElement => {
        return (
            <div
                key={c.id}
            >
                <h4
                    style={{
                        textAlign: 'center'
                    }}
                    className={c.answer ? "teacher-answer-choice" : "teacher-normal-choice"}
                >{c.value}</h4>
            </div>
        );
    };

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

export default OnGoingQCM;
