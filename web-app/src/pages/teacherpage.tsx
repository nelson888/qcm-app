import React, {Component} from "react";
import './teacherpage.scss';
import {History} from "history";
import {QcmAllResponse, QcmClient} from "../services/qcmClient";
import {Qcm} from "../types";
import QcmList from "../components/qcmlist";
import {toast} from "react-toastify";
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
        const {current} = this.state;
        return (
            <div style={{
                width: '100%',
                height: '100%'
            }}>

            </div>
        );
    }
}

export default TeacherPage;
