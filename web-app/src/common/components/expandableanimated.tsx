import React, {Component} from 'react';
import AnimateHeight from 'react-animate-height';

type Props = {
    height?: number,
    expandedHeight?: 'auto' | number,
    duration?: number,
    children: any,
    expanded: boolean,
    paddingLeft?: number,
    paddingRight?: number,
    paddingTop?: number,
    paddingBottom?: number
}

class ExpandableAnimated extends Component<Props, {}> {

    render() {
        const {children, height=0, duration, expandedHeight='auto', expanded,
            paddingLeft, paddingBottom, paddingRight, paddingTop} = this.props;
        return(
            <AnimateHeight
                height={expanded ? expandedHeight : height}
                duration={duration}

            >
                <div
                    style={{
                        paddingLeft,
                        paddingBottom,
                        paddingRight,
                        paddingTop
                    }}
                >
                {children}
                </div>
            </AnimateHeight>
        );
    }
}

export default ExpandableAnimated;
