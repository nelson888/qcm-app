import React, {Component} from "react";
import './teacherpage.scss';
import {History} from "history";

type State = {

};

type Props = {
    history: History,
}

class TeacherPage extends Component<Props, State> {

    render(): React.ReactElement {
        return (
            <div>
                <h1>TEACHER PAGE</h1>

            </div>
        );
    }
}

export default TeacherPage;
