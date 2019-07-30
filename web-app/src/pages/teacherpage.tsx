import React, {Component} from "react";
import './teacherpage.scss';
import {History} from "history";
import {QcmClient} from "../services/apiClient";

type State = {

};

type Props = {
    history: History,
    apiClient: QcmClient
}

class TeacherPage extends Component<Props, State> {

    componentDidMount(): void {
        this.props.apiClient.getQcms();
    }

    render(): React.ReactElement {
        return (
            <div>
                <h1>TEACHER PAGE</h1>

            </div>
        );
    }
}

export default TeacherPage;
