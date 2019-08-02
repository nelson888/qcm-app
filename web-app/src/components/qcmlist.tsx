import React,{Component} from "react";
import {Qcm, QcmState} from "../types";
import './qcmlist.scss';

const finishedIcon = require('../images/icons/finished.png');
const incompleteIcon = require('../images/icons/incomplete.png');
const startedIcon = require('../images/icons/ongoing.png');
const completeIcon = require('../images/icons/complete.png');

type Props = {
    qcms: Qcm[],
    onClick(qcm: Qcm): void
};
class QcmList extends Component<Props, {}> {

    render(): React.ReactElement {
        const {qcms} = this.props;
        return (
            <ul>
                {
                    qcms.map(this.toComponent)
                }
            </ul>
        );
    }

    toComponent = (qcm: Qcm): React.ReactElement => {
        return (
            <li key={qcm.id.toString()} className="no-margin no-padding"
                style={{
                    height: 50,
                    width: '80%'
                }}
                onClick={() => this.props.onClick(qcm)}
            >
                <div
                    className="qcm-element"
                >
                    <h3
                        className="no-padding no-margin inline vertical-middle unselectable"
                    >{qcm.name ? qcm.name : 'New MCQ (no name)'}</h3>

                    <img
                        className="inline vertical-middle unselectable"
                        style={{
                            width: 32,
                            height: 32,
                            marginLeft: 40
                        }}
                        alt={qcm.state.toLowerCase()}
                        src={this.getIcon(qcm.state)}
                    />

                    <div
                        className="black-line"
                        style={{
                            marginTop: 8
                        }}
                    />
                </div>
            </li>
        );
    };

    private getIcon(state: QcmState): string {
        switch (state) {
            default:
            case "COMPLETE":
                return completeIcon;
            case "FINISHED":
                return finishedIcon;
            case "INCOMPLETE":
                return incompleteIcon;
            case "STARTED":
                return startedIcon;
        }
    }
}

export default QcmList;
