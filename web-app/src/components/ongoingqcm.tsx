import React,{Component} from "react";
import LoadingScreen from "../common/components/loadingscreen";
import {Choice, Qcm, Question} from "../types";
import './ongoingqcm.scss';

type Props = {
    qcm: Qcm
};

type State = {
    loading: boolean,
    index: number
};

class OnGoingQCM extends Component<Props, State> {

    state: State = {
        loading: false,
        index: 0
    };
    render() {
        const {loading, index} = this.state;
        const {qcm} = this.props;

        let question: Question|null = null;
        if (index >= 0 && index < qcm.questions.length) {
            question = qcm.questions[index];
        }
        const isLast = index === qcm.questions.length - 1;
        return (
            <LoadingScreen
                active={loading}
                message={"Loading next question..."}
            >
                <p>Question {index + 1} out of {qcm.questions.length}</p>
                {this.renderQuestion(question)}
                <div
                    style={{
                        position: 'absolute',
                        bottom: '25%',
                        left: '75%'
                    }}
                >
                    <button>
                        {isLast ? "Finish" : "Next Question"}
                    </button>
                </div>
            </LoadingScreen>
        );
    }

    private renderQuestion = (question: Question | null): React.ReactElement => {
        if (!question) {
            return <div/>;
        }
        return (
            <div
                className="screen-centered"
            >
                <h1>{question.question}</h1>
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
                    className={c.answer ? "teacher-answer-choice" : "teacher-normal-choice"}
                >{c.value}</h4>
            </div>
        );
    }
}

export default OnGoingQCM;
