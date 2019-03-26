
import { NativeModules, AppRegistry } from 'react-native';
import EventEmitter from 'EventEmitter';

const { RNHoverReactSdk } = NativeModules;

TransactionUpdate = async (data) => {
	var emitter = new EventEmitter();
	emitter.emit("transaction_update", data, false);
}
AppRegistry.registerHeadlessTask('TransactionUpdate', () => TransactionUpdate);

export default RNHoverReactSdk;