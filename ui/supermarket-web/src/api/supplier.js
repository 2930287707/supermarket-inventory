import request from '@/utils/request'

export function getSupplierList() {
  return request({
    url: '/supplier/list',
    method: 'get'
  })
}

export function addSupplier(data) {
  return request({
    url: '/supplier/add',
    method: 'post',
    data
  })
}

export function updateSupplier(data) {
  return request({
    url: '/supplier/update',
    method: 'post',
    data
  })
}

export function deleteSupplier(id) {
  return request({
    url: '/supplier/delete/' + id,
    method: 'delete'
  })
}