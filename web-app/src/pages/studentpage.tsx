import React, {Component} from "react";
import './studentpage.scss';
import {History} from "history";


type State = {

};

type Props = {
    history: History,
}

class StudentPage extends Component<Props, State> {

    render(): React.ReactElement {
        return (
            <div>
                <h1>STUDENT PAGE</h1>
            </div>
        );
    }
}

export default StudentPage;

