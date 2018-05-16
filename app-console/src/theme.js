import getMuiTheme from 'material-ui/styles/getMuiTheme';
import baseTheme from 'material-ui/styles/baseThemes/lightBaseTheme';
import * as Colors from 'material-ui/styles/colors';
import { fade } from 'material-ui/utils/colorManipulator'

export const getTheme = () => {
  let overwrites = {
    "tabs": {
        "backgroundColor": Colors.grey600
    }
};
  return getMuiTheme(baseTheme, overwrites);
}