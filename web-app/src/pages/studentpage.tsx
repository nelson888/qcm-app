import React from "react";
import './studentpage.scss';
import {History} from "history";
import {Qcm} from "../types";
import {QcmClient} from "../services/qcmClient";
import LoggedPage from "./loggedpage";


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

class StudentPage extends LoggedPage<Props, State> {

    renderQcm(qcm: Qcm): React.ReactElement {

        switch (qcm.state) {

            default:
                return (
                    <div>
                        <h1>STUDENT PAGE</h1>
                    </div>
                );
        }
    }
}

export default StudentPage;

