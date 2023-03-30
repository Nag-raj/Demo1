import ReactDOM from 'react-dom';
import componentMap from './component-map';
import { Provider } from 'react-redux';
import { store } from './store';
import { locale, messages } from './i18n/en';

function render(componentKey, Component) {
    document.createElement(componentKey);
    const nodes = Array.from(document.getElementsByTagName(componentKey));
    nodes.map((node, i) => {
        const attrs = Array.prototype.slice.call(node.attributes);
        const props = {key: `${componentKey}-${i}`};
        attrs.map(attr => {
            props[attr.name] = attr.value;
            return null;
        }
    );
    if (!node.attributes.bootstraped) {
        ReactDOM.render(
        <Provider store={store}>
            <Component {...props} locale={locale} messages={messages}/>
        </Provider>, node);
        node.setAttribute("bootstraped", !0);
    }});
}

function BootStrapReactComp() {
    Object.keys(componentMap).forEach (key => {
        var Component = componentMap[key];
        render(key, Component);
    });
}

BootStrapReactComp();

