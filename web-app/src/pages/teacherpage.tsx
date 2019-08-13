import React from "react";
import './teacherpage.scss';
import {History} from "history";
import {QcmAllResponse, QcmClient, QcmResponse, VoidResponse} from "../services/qcmClient";
import {Choice, Qcm, Question} from "../types";
import {confirmAlert} from "react-confirm-alert";
import LoggedPage from "./loggedpage";
import QcmForm from "../components/qcmform";
import {toast} from "react-toastify";
import OnGoingQCMTeacher from "../components/ongoingqcmteacher";
import QcmResultComponent from "../components/qcmresultcomponent";

type State = {
    qcms: Qcm[],
    current: Qcm | null,
    modifying: boolean,
    loading: boolean
};

type Props = {
    history: History,
    apiClient: QcmClient
}

class TeacherPage extends LoggedPage<Props, State> {

    state: State = {
        qcms: [],
        current: null,
        modifying: false,
        loading: false
    };

    renderQcm(qcm: Qcm): React.ReactElement {
        switch (qcm.state) {
            case "COMPLETE":
                if (this.state.modifying) {
                    return (
                        <div
                            className="full-width"
                            style={{
                                margin: 16
                            }}
                        >
                            <QcmForm
                                qcm={qcm}
                                onCancel={() => this.setState({modifying: false})}
                                onSubmit={this.updateQcm}/>
                        </div>
                    );
                }
                return (
                    <div
                        className="full-width"
                        style={{
                            margin: 16
                        }}
                    >
                        <h1>{qcm.name}</h1>
                        <p>Status: complete MCQ (not started yet)</p>
                        {this.renderQuestions(qcm.questions)}

                        <div
                            style={{
                                marginTop: 32,
                                marginLeft: 20
                            }}
                        >
                            <button
                                className="inline btn-grad"
                                onClick={() => this.startQcm(qcm)}
                            >Start</button>

                            <button
                                className="inline btn-grad"
                                style={{
                                    margin: 32
                                }}
                                onClick={() => this.setState({modifying: true })}
                            >Modify</button>

                            <button
                                className="inline btn-grad-danger"
                                onClick={() => this.onDeleteQcm(qcm)}
                            >Delete</button>
                        </div>
                    </div>
                );
            case "INCOMPLETE":
                return (
                    <div
                        className="full-width"
                    >
                        <QcmForm
                            qcm={qcm}
                            onCancel={() => this.setState({modifying: false})}
                            onSubmit={this.updateQcm}
                            creating={true}/>
                    </div>
                );
            case "STARTED":
                return <OnGoingQCMTeacher qcm={qcm} apiClient={this.props.apiClient} refresh={this.refresh} />;
            case "FINISHED":
                return <QcmResultComponent apiClient={this.props.apiClient} qcm={qcm}/>;
            default:
                return (
                    <div
                        className="full-width"
                    >

                    </div>
                );
        }

    }

    private renderQuestions(questions: Question[]): React.ReactElement {
        let i: number = 0;
        return (
            <ul
                className="no-margin no-padding"
            >
                {questions.map((q: Question) => (
                    <li key={q.id}
                        className="no-margin no-padding full-width"
                    >
                        <h3>Question n°{++i}: {q.question}</h3>
                        {this.renderChoices(q.choices)}
                    </li>
                ))}
            </ul>
        );
    }

    private updateQcm = (qcm: Qcm): void => {
        this.loadingMessage = "Updating MCQ...";
        this.setState({loading: true});
        this.props.apiClient.updateQcm(qcm)
            .then((response:QcmResponse) => {
                this.setState({loading: false, modifying: false});
                if (response.isSuccess) {
                    let qcms: Qcm[] = [...this.state.qcms];
                    let index:number =qcms.findIndex(q => q.id === response.successData.id);
                    qcms[index] = response.successData;
                    this.setState({qcms});
                    toast.success(`Successfully updated ${response.successData.name}`);
                    this.refresh();
                } else {
                    toast.error(`Couldn't update qcm: ${response.errorData}`)
                }
            })
            .catch((error: any) => {
                this.setState({loading: false});
                toast.error("An error occurred: " + error.toString());
            })
    };
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
                            className={c.answer ? "teacher-answer-choice" : "teacher-normal-choice"}
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

    private deleteQcm(qcm: Qcm): void { //TODO seems to throw error (unexpected output end)
        this.setState({loading: true});
        this.props.apiClient.deleteQcm(qcm.id)
            .then((response: QcmResponse) => {
                this.setState({loading: false});
                if (response.isSuccess) {
                    let qcms: Qcm[] = [...this.state.qcms].filter(qu => qu.id !== response.successData.id);
                    this.setState({ qcms });
                    toast.success(`Successfully deleted ${response.successData.name}`)
                } else {
                    toast.error(`Couldn't delete qcm: ${response.errorData}`)
                }
            })
            .catch((error: any) => {
                this.setState({loading: false});
                toast.error("An error occurred: " + error.toString());
            });
    }

    onQcmClick = (qcm: Qcm) => {
        const {current} = this.state;
        if (current && qcm.id !== current.id && current.state === "INCOMPLETE") {
            confirmAlert({
                title: `Attention, by changing MCQ you will loose all the data of this MCQ that hasn't been saved`,
                buttons: [
                    {
                        label: 'Ok',
                        onClick: () => this.setState({current: qcm, modifying: false})
                    },
                    {
                        label: 'Cancel',
                        onClick: () => null
                    }
                ]
            });
        } else {
            this.setState({current: qcm, modifying: false});
        }
    };

    startQcm = (qcm: Qcm) => {
        this.loadingMessage = "Starting qcm...";
        this.setState({loading: true});
        this.props.apiClient.launchQcm(qcm.id)
            .then((response: VoidResponse) => {
                this.setState({loading: false});
                if (response.isSuccess) {
                    let qcms = [...this.state.qcms];
                    qcms.filter(q => q.id === qcm.id)[0].state = "STARTED";
                    this.setState({qcms});
                } else {
                    toast.error("Couldn't start the MCQ: " + response.errorData);
                }
            }).catch((error: any) => {
            this.setState({loading: false});
            toast.error("An error occurred: " + error.toString());
        });
    };

    fetchQcms(apiClient: QcmClient): Promise<QcmAllResponse> {
        return this.props.apiClient.getMyQcms();
    }
}

export default TeacherPage;
