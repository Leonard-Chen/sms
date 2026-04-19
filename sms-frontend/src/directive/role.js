import {getUser} from '../utils/user.js'

//没有权限（角色）时隐藏某些按钮
export default {
    mounted(el, binding) {
        const user = getUser()
        const {value} = binding

        if (value && !user?.roles.includes(value)) {
            el.parentNode.removeChild(el)
        }
    }
}