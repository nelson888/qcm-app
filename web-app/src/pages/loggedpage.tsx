import React, {Component} from "react";
import {History} from "history";
import {QcmAllResponse, QcmClient, QcmResponse, TEACHER} from "../services/qcmClient";
import {Qcm} from "../types";
import QcmList from "../components/qcmlist";
import {toast} from "react-toastify";
import './loggedpage.scss';
import LoadingScreen from "../common/components/loadingscreen";

type State = {
    qcms: Qcm[],
    current: Qcm | null,
    loading: boolean
};

type Props = {
    history: History,
    apiClient: QcmClient
}

abstract class LoggedPage<P extends Props, S extends State> extends Component<P, S> {

    loadingMessage: string = "Loading";

    componentDidMount(): void {
        this.fetchQcms(this.props.apiClient)
            .then((response: QcmAllResponse) => {
                if (response.isSuccess) {
                    this.setState({qcms: response.successData});
                } else {
                    toast.error(`error while fetching MCQs: ${response.errorData}`);
                }
            })
            .catch((error) => {
                toast.error("An error occurred: " + error.toString());
            });
    }

    refresh = () => {
        const currentId: number = this.state.current ? this.state.current.id : -1;
        this.fetchQcms(this.props.apiClient)
            .then((response: QcmAllResponse) => {
                if (response.isSuccess) {
                    let current: Qcm | null = null;
                    let idQcm: Qcm[] = response.successData.filter(q => q.id === currentId);
                    if (idQcm.length) {
                        current = idQcm[0];
                    }
                    this.setState({qcms: response.successData, current });

                } else {
                    toast.error(`error while fetching MCQs: ${response.errorData}`);
                }
            })
            .catch((error) => {
                toast.error("An error occurred: " + error.toString());
            });
    }

    abstract fetchQcms(apiClient: QcmClient): Promise<QcmAllResponse>;
    abstract renderQcm(qcm: Qcm): React.ReactElement;

    render(): React.ReactElement {
        const {qcms, current, loading} = this.state;
        return (
            <LoadingScreen
                active={loading}
                message={this.loadingMessage}
            >
                <div
                    className="qcms-list"
                >
                    <QcmList qcms={qcms}
                             onClick={this.onQcmClick}
                    />
                    {
                        this.props.apiClient.getRole() === TEACHER &&
                        <p
                            className="qcm-element no-margin unselectable"
                            style={{
                                padding: 5,
                                textAlign: 'center',
                                verticalAlign: 'middle'
                            }}
                            onClick={this.createQcm}
                        >Create new mcq</p>
                    }
                </div>
                <div
                    className="logged-content"
                >
                    {
                        !current && <h2
                            style={{
                                width: '50%',
                                marginTop: '25%',
                                textAlign: 'center'
                            }}
                            className="center-fixed-width"
                        >Select a MCQ</h2>
                    }
                    {
                        current && this.renderQcm(current as Qcm)
                    }
                </div>
            </LoadingScreen>
        );
    }


    private createQcm = () => {
        this.loadingMessage = "Creating new qcm...";
        this.setState({loading: true});
        this.props.apiClient
            .newQcm()
            .then((response: QcmResponse) => {
                this.setState({loading: false});
                if (response.isSuccess) {
                    this.setState({
                        qcms: this.state.qcms.concat(response.successData)
                    })
                } else {
                    toast.error(`Couldn't create new qcm: ${response.errorData}`);
                }
            })
            .catch((error: any) => {
                this.setState({loading: false});
                toast.error("An error occurred: " + error.toString());
            })
    };

    protected onQcmClick = (qcm: Qcm) => this.setState({current: qcm});

}

export default LoggedPage;
