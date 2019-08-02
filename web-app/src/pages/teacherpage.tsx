import React, {Component} from "react";
import './teacherpage.scss';
import {History} from "history";
import {QcmAllResponse, QcmClient} from "../services/qcmClient";
import {Choice, Qcm, Question} from "../types";
import {confirmAlert} from "react-confirm-alert";
import LoggedPage from "./loggedpage";

type State = {
    qcms: Qcm[],
    current: Qcm | null
};

type Props = {
    history: History,
    apiClient: QcmClient
}

class TeacherPage extends LoggedPage<Props, State> {

    state: State = {
        qcms: [],
        current: null
    };

    renderQcm(qcm: Qcm): React.ReactElement {
        switch (qcm.state) {
            case "COMPLETE":
                return (
                    <div style={{
                        width: '100%',
                        height: '100%'
                    }}>
                        <h1>{qcm.name}</h1>
                        <p>Status: completed (not started yet)</p>
                        {this.renderQuestions(qcm.questions)}

                        <div
                            className="full-width"
                            style={{
                                marginTop: 32
                            }}
                        >
                            <button
                                className="inline"
                            >Start</button>

                            <button
                                className="inline"
                                onClick={() => this.onDeleteQcm(qcm)}
                            >Delete</button>
                        </div>
                    </div>
                );
                case "INCOMPLETE":
                    return (
                        <div style={{
                            width: '100%',
                            height: '100%'
                        }}>

                        </div>
                    );
            default:
                return (
                    <div style={{
                        width: '100%',
                        height: '100%'
                    }}>

                    </div>
                );
        }

    }

    private renderQuestions(questions: Question[]): React.ReactElement {
        return (
            <ul
                className="no-margin no-padding"
            >
                {questions.map((q: Question) => (
                    <li key={q.id}
                        className="no-margin no-padding full-width"
                    >
                        <h3>{q.question}</h3>
                        {this.renderChoices(q.choices)}
                    </li>
                ))}
            </ul>
        );
    }

    private renderChoices(choices: Choice[]): React.ReactElement {
        return (
            <div
                className="choices-view-grid"
            >
                {choices.map((c: Choice) => (
                    <div
                        key={c.id}
                    >
                        <p
                            className={c.answer ? "answer-choice" : "normal-choice"}
                        >{c.value}</p>
                    </div>
                ))}

            </div>
        )
    }

    private onDeleteQcm(qcm: Qcm) {
        confirmAlert({
            title: `Do you want to delete '${qcm.name}'?`,
            buttons: [
                {
                    label: 'Yes',
                    onClick: () => this.deleteQcm(qcm)
                },
                {
                    label: 'Cancel',
                    onClick: () => null
                }
            ]
        });
    }

    private deleteQcm(qcm: Qcm): void {
        //TODO
    }
}

export default TeacherPage;
