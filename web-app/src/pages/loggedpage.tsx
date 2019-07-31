import React, {Component} from "react";
import {History} from "history";
import {QcmAllResponse, QcmClient} from "../services/qcmClient";
import {Qcm} from "../types";
import QcmList from "../components/qcmlist";
import {toast} from "react-toastify";
import './loggedpage.scss';

type State = {
    qcms: Qcm[]
};

type Props = {
    history: History,
    apiClient: QcmClient
}

abstract class LoggedPage<P extends Props, S extends State> extends Component<P, S> {

    componentDidMount(): void {
        this.props.apiClient.getQcms()
            .then((response: QcmAllResponse) => {
                if (response.isSuccess) {
                    this.setState({qcms: response.successData });
                } else {
                    toast.error(`error while fetching QMCs: ${response.errorData}`);
                }
        })
            .catch((error) => {
                toast.error("An error occurred: " + error.toString());
            });
    }

    abstract content(): React.ReactElement;

    render(): React.ReactElement {
        const {qcms} = this.state;
        return (
            <React.Fragment>
                <div
                    className="qcms-list"
                >
                    <QcmList qcms={qcms} />
                </div>
                <div
                    className="logged-content"
                >
                    {this.content()}
                </div>
            </React.Fragment>
        );
    }
}

export default LoggedPage;
