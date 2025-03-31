<!-- 用户中心 -->
<template>
  <div>
    <wbc-nav></wbc-nav>
    <div class="container">
      <!-- 展示模式 -->
      <div v-if="!isEdit" class="profile-view">
        <div class="profile-header">
          <h1>个人中心</h1>
          <el-button
            type="primary"
            icon="el-icon-edit"
            @click="enterEditMode"
            class="edit-button"
          >
            编辑资料
          </el-button>
        </div>

        <div class="tcommonBox user-profile">
          <!-- 头像展示 -->
          <div class="avatar-section">
            <img
              :src="userInfo.avatar || 'static/img/default-avatar.jpg'"
              class="profile-avatar"
              :onerror="$store.state.errorImg"
            />
          </div>

          <!-- 个人信息 -->
          <div class="user-info">
            <div class="info-item">
              <span class="info-label">昵称：</span>
              <span class="info-content">{{ userInfo.nickName }}</span>
            </div>
            <!--            <div class="info-item">-->
            <!--              <span class="info-label">动态：</span>-->
            <!--              <span class="info-content">{{ userInfo.postCount }}条</span>-->
            <!--            </div>-->
            <div class="info-item">
              <span class="info-label">签名：</span>
              <p class="signature">{{ userInfo.signature || "暂无签名" }}</p>
            </div>
            <div class="info-item">
              <span class="info-label">邮箱：</span>
              <span class="info-content">{{ userInfo.email }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">性别：</span>
              <span class="info-content">{{ genderMap[userInfo.sex] }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 编辑模式 -->
      <div v-else class="edit-view">
        <div class="edit-header">
          <h1>编辑个人资料</h1>
          <div class="action-buttons">
            <el-button @click="cancelEdit">取消</el-button>
            <el-button type="primary" @click="saveUserInfo">保存</el-button>
          </div>
        </div>

        <div class="tcommonBox">
          <!-- 原有编辑表单内容 -->
          <section>
            <ul class="userInfoBox">
              <!-- 头像上传 -->
              <li class="avatarlist">
                <span class="leftTitle">头像</span>
                <el-upload
                  class="avatar-uploader"
                  name="img"
                  :action="uploadURL"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                  :before-upload="beforeAvatarUpload">
                  <img v-if="userInfo.avatar"
                       :src="userInfo.avatar"
                       class="avatar"
                       :onerror="$store.state.errorImg"
                  >
                  <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                  <div slot="tip" class="el-upload__tip">支持JPG/PNG格式，小于2MB</div>
                </el-upload>
              </li>

              <!-- 昵称 -->
              <li class="username">
                <span class="leftTitle">昵称</span>
                <el-input
                  v-model="userInfo.nickName"
                  placeholder="请输入昵称"
                  :maxlength="16"
                  show-word-limit
                ></el-input>
                <i class="fa fa-wa fa-asterisk required-icon"></i>
              </li>

              <!-- 个性签名 -->
              <li>
                <span class="leftTitle">个性签名</span>
                <el-input
                  type="textarea"
                  :rows="2"
                  v-model="userInfo.signature"
                  placeholder="请输入个性签名"
                  :maxlength="30"
                  show-word-limit
                ></el-input>
              </li>

              <!-- 邮箱（不可编辑） -->
              <li>
                <span class="leftTitle">电子邮件</span>
                <span class="readonly-field">{{ userInfo.email }}</span>
              </li>

              <!-- 性别 -->
              <li>
                <span class="leftTitle">性别</span>
                <el-radio-group v-model="userInfo.sex">
                  <el-radio label="0">男</el-radio>
                  <el-radio label="1">女</el-radio>
                </el-radio-group>
              </li>
            </ul>

            <!-- 保存按钮 -->
            <div class="saveInfobtn">
              <el-button
                type="primary"
                size="large"
                @click="saveUserInfo"
                :loading="isSaving"
              >
                {{ isSaving ? '保存中...' : '保存修改' }}
              </el-button>
            </div>
          </section>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import header from '../components/header.vue'
import {getUserInfo,savaUserInfo} from '../api/user.js'//获取用户信息，保存用户信息
import store from '../store'
export default {
  name: 'UserInfo',
  data() { //选项 / 数据
    return {
      uploadURL:'',
      isEdit: false,
      userInfo:{},//本地存储的用户信息
      userInfoObj:'',//用户的信息
      rules: {
        // 新增验证规则（根据需要添加到对应表单元素）
        signature: [
          { max: 30, message: '不能超过30个字符', trigger: 'blur' }
        ]
      }
    };
  },
  methods: { //事件处理器
    handleAvatarSuccess(res, file) {//上传头像
      if(res.code == 200){
        this.userInfoObj.avatar = res.data;
        this.userInfoObj.head_start = 1;
      }else{
        this.$message.error('上传图片失败');
      }

    },
    beforeAvatarUpload(file) {//判断头像的内存大小
      const isJPG = file.type == 'image/png'||file.type=='image/jpg'||file.type=='image/jpeg';
      const isLt2M = file.size / 1024 / 1024 < 2;
      // console.log(file);
      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG/PNG 格式!');
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!');
      }
      return isJPG && isLt2M;
    },

    saveInfoFun: function(){//保存编辑的用户信息
      var that = this;

      if(!that.userInfoObj.nickName){ //昵称为必填
        that.$message.error('昵称为必填项，请填写昵称');
        return;
      }
      if(!that.userInfoObj.sgnature){ //  个性签名为(可选)
        //可选项，不用判断是否为空
        savaUserInfo(that.userInfoObj).then((response)=>{//保存信息接口，返回展示页
          that.$message.success( '保存成功！');
          that.isEdit = false;
          that.routeChange() ;
        })
        return;
      }
      // if(!that.userInfoObj.password){ //密码为必填
      //      that.$message.error('密码为必填项，请填写密码');
      //       return;
      // }


      savaUserInfo(that.userInfoObj).then((response)=>{//保存信息接口，返回展示页
        that.$message.success( '保存成功！');
        that.isEdit = false;
        that.routeChange() ;
      })
    },
    routeChange: function(){//展示页面信息
      var that = this;
      // console.log(this.$router,this.$route);
      if(localStorage.getItem('userInfo')){
        that.haslogin = true;
        that.userInfo = JSON.parse(localStorage.getItem('userInfo'));
        that.userId = that.userInfo.id;
        getUserInfo(that.userId).then((response)=>{
          that.userInfoObj = response;
          that.userInfoObj.head_start = 0;
        })
      }else{
        that.haslogin = false;
      }

    }
  },
  components: { //定义组件
    'wbc-nav':header,
  },
  watch: {
    // 如果路由有变化，会再次执行该方法
    '$route':'routeChange'
  },
  created() { //生命周期函数
    this.routeChange();
    this.uploadURL = store.state.baseURL+'upload'
  }
}
</script>

<style>
.userInfoBox .avatarlist{
  position: relative;
}

.avatar-uploader {
  display: inline-block;
  vertical-align: top;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 120px;
  height: 120px;
}
.avatar-uploader .el-upload:hover {
  border-color: #20a0ff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
  position: absolute;
  top:0;
  left:0;
}
.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: block;
  object-fit: cover;
}
.gotoEdit{
  font-size: 15px;
  float:right;
  cursor: pointer;
  color:#999;
}
.gotoEdit:hover {
  color:#333;
}
/*个人设置*/
.userInfoBox .leftTitle{
  display: inline-block;
  width:100px;
  padding: 10px 0;
}
.userInfoBox .rightInner{
  display: inline-block;
  max-width: calc(100% - 140px);
  vertical-align: top;
}
.userInfoBox li{
  padding:20px;
  border-bottom: 1px solid #ddd;
}
.userInfoBox li:last-child{
  border-bottom: 1px solid transparent;
}
.userInfoBox  .el-input,.userInfoBox  .el-textarea{
  max-width:300px;
  min-width: 100px;
}

.userInfoBox .el-input__inner{
  border-radius: 4px;
}
.userInfoBox  .el-textarea{
  vertical-align: top;
}
.saveInfobtn{
  margin: 20px 0;
  text-align: center;
}
.saveInfobtn a{
  color:#fff;
  padding:6px 20px;
  margin:5px 10px;
  border-radius: 5px;
  font-size: 14px;
}
.userInfoBox .fa-asterisk{
  color: #DF2050;
}
.signature-view {
  padding: 15px;
  margin: 20px 0;
  border-left: 3px solid #409EFF;
  background: #f8f9fa;
}

.signature-view p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.empty-signature {
  color: #999;
  font-style: italic;
}

</style>
