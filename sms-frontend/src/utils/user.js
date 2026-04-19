export const getUser = () => {
    const user = JSON.parse(localStorage.getItem('user') || 'null')
    if (user) {
        user.roles = (user?.authorities || []).map(a => a.role || a).filter(Boolean)
    }

    return user
}