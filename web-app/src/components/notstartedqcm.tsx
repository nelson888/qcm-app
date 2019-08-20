import React, {Component} from 'react';
import {Qcm} from "../types";
import {clearInterval, setInterval} from "timers";
import {QcmClient, QcmResponse} from "../services/qcmClient";

type Props = {
    qcm: Qcm,
    onRefresh(): void,
    apiClient: QcmClient
};

class NotStartedQCMComponent extends Component<Props, {}> {

    intId: any|null = null;

    componentDidMount(): void {
        this.intId = setInterval(this.checkQcm, 800);
    }

    componentWillUnmount(): void {
        if (this.intId != null) {
            clearInterval(this.intId);
        }
    }

    render() {
        const {qcm} = this.props;
        return (
            <React.Fragment>
                <div
                    style={{
                        marginTop: 64
                    }}
                    className="center-horizontal text-center"
                >
                    <h1>{qcm.name}</h1>
                    <p
                        style={{
                            fontSize: 20
                        }}
                    >This MCQ hasn't started yet</p>
                </div>
            </React.Fragment>
        );
    }
    checkQcm = () => {
        const {qcm, apiClient, onRefresh} = this.props;
        apiClient.getQcm(qcm.id)
            .then((response:QcmResponse) => {
                if (response.isSuccess) {
                    if (response.successData.state !== qcm.state) {
                        clearInterval(this.intId);
                        this.intId = null;
                        onRefresh();
                    }
                } else {
                    clearInterval(this.intId);
                    this.intId = null;
                    onRefresh();
                }
            });
    }


}

export default NotStartedQCMComponent;
