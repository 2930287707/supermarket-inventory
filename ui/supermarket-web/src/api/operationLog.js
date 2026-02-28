import request from '@/utils/request'

export function getOperationLogPage(params) {
  return request({
    url: '/operation-log/list',
    method: 'get',
    params
  })
}
