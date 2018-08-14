import * as React from "react"
import PropTypes from "prop-types"
import axios from "axios"
import {
	StyleSheet,
	Text,
	View,
	ViewPropTypes,
	NativeModules,
	Alert,
	DeviceEventEmitter,
	requireNativeComponent,
	Dimensions,
	TouchableOpacity,
	ScrollView
} from "react-native"

const NativeHelloWorld = NativeModules.HelloWorld
const LytCalendarView = requireNativeComponent(
	"LytCalendarView",
	{
		name: "LytCalendarView",
		propTypes: {
			cities: PropTypes.array,
			hotCities: PropTypes.array,
			selected: PropTypes.array,
			...ViewPropTypes
		}
	},
	{ nativeOnly: { onChange: true } }
)

export default class App extends React.Component<{}> {
	state = {
		areas: [],
		loaded: false,
		selected: []
	}

	componentDidMount() {
		return axios
			.get("http://ymdev.baixing.com.cn/mili/api/areas")
			.then(({ data }) => {
				const areas = []
				for (const section of data) {
					for (const city of section.children) {
						areas.push({
							name: city.name,
							pinyin: city.pinyin
							// selected: false
						})
					}
				}
				this.setState({ areas, loaded: true })
			})
		// DeviceEventEmitter.addListener("lytNativeEvent", e => {
		// 	console.log("Event greeting has been sent to ")
		// 	console.log(e)
		// })
		// Alert.alert(NativeHelloWorld.MODULE_OWNER_NAME, "", [
		// 	{
		// 		text: "OK",
		// 		onPress: () => {
		// 			// NativeHelloWorld.greeting('callback', (name: string) => {
		// 			//   console.log('Greeting has been sent to ' + name)
		// 			// })
		// 			// NativeHelloWorld.asyncGreeting('async').then((name: string) => {
		// 			//   console.log('Async greeting has been sent to ' + name)
		// 			// })
		// 			NativeHelloWorld.eventGreeting("event")
		// 		}
		// 	}
		// ])
	}

	onSelect = event => {
		const { city } = event.nativeEvent
		const set = new Set<string>(this.state.selected)
		if (set.has(city)) {
			set.delete(city)
		} else {
			set.add(city)
		}
		console.log(Array.from(set))
		this.setState({ selected: Array.from(set) })
	}

	render() {
		const { width, height } = Dimensions.get("window")
		const { loaded, areas, selected } = this.state
		return (
			<View style={{ paddingBottom: 20 }}>
				<ScrollView
					style={{ width: "100%", height: 50 }}
					horizontal={true}
				>
					{selected.map(item => (
						<Text style={{ margin: 10 }} key={item}>
							{item}
						</Text>
					))}
				</ScrollView>
				{!loaded ? (
					<Text>Loading...</Text>
				) : (
					<LytCalendarView
						height={height - 50}
						width={width}
						cities={areas}
						hotCities={[
							"上海",
							"广州",
							"重庆",
							"成都",
							"武汉",
							"苏州",
							"北京",
							"昆明",
							"西安",
							"青岛",
							"深圳",
							"南京"
						]}
						selected={selected}
						onChange={this.onSelect}
					/>
				)}
			</View>
		)
	}
}

const styles = StyleSheet.create({
	container: {
		flex: 1,
		backgroundColor: "#fff",
		alignItems: "center",
		justifyContent: "center"
	}
})
