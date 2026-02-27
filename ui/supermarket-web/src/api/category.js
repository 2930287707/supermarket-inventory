import request from '@/utils/request'

// 获取分类列表
export function getCategoryList() {
  return request({
    url: '/category/list',
    method: 'get'
  })
}

// 新增分类
export function addCategory(data) {
  return request({
    url: '/category/add',
    method: 'post',
    data
  })
}

// 修改分类
export function updateCategory(data) {
  return request({
    url: '/category/update',
    method: 'post',
    data
  })
}

// 删除分类
export function deleteCategory(id) {
  return request({
    url: '/category/delete/' + id,
    method: 'delete'
  })
}
