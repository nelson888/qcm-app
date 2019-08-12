import React, {Component} from "react";
import './qcmresultcomponent.scss';
import {Qcm, QcmResult, QuestionResult} from "../types";
import {QcmClient, ResultResponse} from "../services/qcmClient";
import {toast} from "react-toastify";
const correctIcon = require('../images/icons/true.png');
const incorrectIcon = require('../images/icons/false.png');
const emptyIcon = require('../images/icons/empty.png');

type Props = {
    apiClient: QcmClient,
    qcm: Qcm
};

type State = {
    loading: boolean,
    result: QcmResult|null
}

class QcmResultComponent extends Component<Props, State> {

    state: State = {
        loading: true,
        result: null
    };

    componentDidMount(): void {
        const {apiClient, qcm} = this.props;
        apiClient.getResult(qcm.id)
            .then((response: ResultResponse) => {
                if (response.isSuccess) {
                    this.setState({loading: false, result:response.successData});
                } else {
                    toast.error(`Couldn't get MCQ result: ${response.errorData}`)
                }
            })
            .catch((error: any) => {
                this.setState({loading: false});
                toast.error("An error occurred: " + error.toString());
            })
    }

    render() {
        const {result, loading} = this.state;
        const { qcm } = this.props;
        if (loading) {
            return <h1>LOADING</h1>;
        }
        if (result == null) {
            return <div/>;
        }
        return (
            <div
                style={this.getGridStyle(result.questionResults.length + 1)}
            >
                <div/>
                {qcm.questions.map(q =>
                    <p
                        className="result-question"
                        key={q.id}>{q.question}</p>)}

                {result.participants.map(p => this.participantRow(p, result.questionResults))}
            </div>
        );
    }

    participantRow = (p: string, questionResults: QuestionResult[]) => {
        let i: number = 0;
        return (
            <React.Fragment
                key={p}
            >
                <p
                    className="result-participant"
                >{p}</p>
                {questionResults.map(q => q.responses[p]).map(answer => this.responseComponent(i++, answer))}
            </React.Fragment>
        );
    };

    responseComponent = (cId: number, correct: boolean|null) => { //TODO
        const size = 32;
        let alt = 'unanswered';
        if (correct !== null) {
            alt = correct ? 'correct' : 'incorrect';
        }
        return <img
            key={cId}
            alt={alt}
            className="result-answer"
            src={this.answerIcon(correct)}
            style={{
                width: size,
                height: size,
            }}
        />
    };

    getGridTemplateColumns = (nbColumns: number, percentage: string) => {
        let result: string = "";
        for (let i = 0; i < nbColumns; i++) {
            result += percentage + " ";
        }
        return result;
    };
    getGridStyle = (nbColumns: number) => {
        const percentage = 1.0 / nbColumns * 100;
      return {
          display: 'grid',
          gridTemplateColumns: this.getGridTemplateColumns(nbColumns, percentage.toString() + "%")
      };
    };

    answerIcon = (b: boolean|null) => {
        if (b == null) {
            return emptyIcon;
        }
        return b ? correctIcon : incorrectIcon;
    };
}

export default QcmResultComponent;
