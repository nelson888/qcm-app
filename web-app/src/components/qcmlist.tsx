import React,{Component} from "react";
import {Qcm} from "../types";

type Props = {
    qcms: Qcm[]
};
class QcmList extends Component<Props, {}> {

    render(): React.ReactElement {
        const {qcms} = this.props;
        return (
            <ul className="list-group">
                {
                    qcms.map(this.toComponent)
                }
            </ul>
        );
    }

    toComponent = (qcm: Qcm): React.ReactElement => {
        return (
            <li key={qcm.id.toString()} className="no-margin" style={{margin: 0}}>
                <h3>{qcm.name}</h3>
                <p>{qcm.state}</p>
            </li>
        );
    }
}

export default QcmList;
