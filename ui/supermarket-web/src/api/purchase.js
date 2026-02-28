import request from '@/utils/request'

export function getPurchasePage(params) {
  return request({
    url: '/purchase/list',
    method: 'get',
    params
  })
}

export function getPurchaseDetail(id) {
  return request({
    url: `/purchase/detail/${id}`,
    method: 'get'
  })
}

export function createPurchaseOrder(data) {
  return request({
    url: '/purchase/create',
    method: 'post',
    data
  })
}

export function approvePurchaseOrder(id) {
  return request({
    url: `/purchase/approve/${id}`,
    method: 'post'
  })
}
