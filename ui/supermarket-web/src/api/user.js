import request from '@/utils/request'

export function login(data) {
    return request({
        url: '/user/login',
        method: 'post',
        data
    })
}

export function changePassword(data) {
    return request({
        url: '/user/change-password',
        method: 'post',
        data
    })
}

export function getUserList() {
    return request({
        url: '/user/list',
        method: 'get'
    })
}

export function addUser(data) {
    return request({
        url: '/user/add',
        method: 'post',
        data
    })
}

export function updateUser(data) {
    return request({
        url: '/user/update',
        method: 'post',
        data
    })
}

export function updateUserStatus(data) {
    return request({
        url: '/user/status',
        method: 'post',
        data
    })
}

export function resetUserPassword(id) {
    return request({
        url: `/user/reset-password/${id}`,
        method: 'post'
    })
}

export function deleteUser(id) {
    return request({
        url: `/user/delete/${id}`,
        method: 'delete'
    })
}
