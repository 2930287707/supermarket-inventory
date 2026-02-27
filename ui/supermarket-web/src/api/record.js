import request from '@/utils/request'

export function getRecordList() {
  return request({
    url: '/stock/list',
    method: 'get'
  })
}