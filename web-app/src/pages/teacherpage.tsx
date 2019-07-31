import React, {Component} from "react";
import './teacherpage.scss';
import {History} from "history";
import {QcmAllResponse, QcmClient} from "../services/apiClient";
import {Qcm} from "../types";
import QcmList from "../components/qcmlist";
import {toast} from "react-toastify";
import LoggedPage from "./loggedpage";

type State = {
    qcms: Qcm[]
};

type Props = {
    history: History,
    apiClient: QcmClient
}

class TeacherPage extends LoggedPage<Props, State> {

    state: State = {
        qcms: []
    };

    content(): React.ReactElement {
        return (
            <div>
                <h1>TEACHER PAGE</h1>
            </div>
        );
    }
}

export default TeacherPage;
