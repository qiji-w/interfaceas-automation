import request from '@/util/request.js'

export function login(user) {
    return request({
      url: '/account/login',
      method: 'post',
      data:user
    })
  }
export function register(user) {
  return request({
    url: '/account/register',
    method: 'post',
    data:user
  })
}