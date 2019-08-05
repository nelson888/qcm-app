import React from "react";
import './studentpage.scss';
import {History} from "history";
import {Qcm} from "../types";
import {QcmAllResponse, QcmClient} from "../services/qcmClient";
import LoggedPage from "./loggedpage";
import OngoingQCMStudent from "../components/ongoingqcmstudent";


type State = {
    qcms: Qcm[],
    current: Qcm | null,
    loading: boolean
};

type Props = {
    history: History,
    apiClient: QcmClient
}

class StudentPage extends LoggedPage<Props, State> {

    state: State = {
        qcms: [],
        current: null,
        loading: false
    };

    renderQcm(qcm: Qcm): React.ReactElement {
        switch (qcm.state) {
            case "STARTED":
                return <OngoingQCMStudent qcm={qcm} apiClient={this.props.apiClient}/>;
            default:
                return (
                    <div>
                        <h1>STUDENT PAGE</h1>
                    </div>
                );
        }
    }

    fetchQcms(apiClient: QcmClient): Promise<QcmAllResponse> {
        return this.props.apiClient.getQcms();
    }
}

export default StudentPage;

