import React, {Component} from "react";
import './teacherpage.scss';
import {History} from "history";
import {QcmAllResponse, QcmClient} from "../services/apiClient";
import {Qcm} from "../types";
import QcmList from "../components/qcmlist";
import {toast} from "react-toastify";

type State = {
    qcms: Qcm[]
};

type Props = {
    history: History,
    apiClient: QcmClient
}

class TeacherPage extends Component<Props, State> {

    state: State = {
        qcms: []
    };

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

    render(): React.ReactElement {
        const {qcms} = this.state;
        return (
            <div>
                <h1>TEACHER PAGE</h1>

                <QcmList qcms={qcms} />
            </div>
        );
    }
}

export default TeacherPage;
