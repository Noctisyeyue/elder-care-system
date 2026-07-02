<!-- 管理端--子菜单--菜品配置 -->
<template>
  <div class="art-full-height">
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" class="search-form">
        <el-form-item label="膳食名称">
          <el-input v-model="searchQuery" placeholder="请输入膳食名称搜索" clearable @clear="handleSearch"
            style="width: 200px">
            <template #prefix>
              <SvgIcon icon="ri:search-line" :size="16" />
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="openAddDialog">
            <SvgIcon icon="ri:add-line" :size="16" />
            <span>添加膳食</span>
          </el-button>
          <el-button type="danger" :disabled="!multipleSelection.length" @click="handleBatchDelete">
            <SvgIcon icon="ri:delete-bin-line" :size="16" />
            <span>批量删除</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 图片预览 -->
    <el-image-viewer v-if="imagePreviewVisible" :initial-index="0" :url-list="[selectedImageUrl]"
      @close="imagePreviewVisible = false" />
    <!-- 膳食表格 -->
    <el-card class="art-table-card" shadow="never">
      <div class="table-header">
        <span class="table-header-title">膳食列表</span>
      </div>
      <el-table :data="foodList" height="100%" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column label="图片" width="80">
          <template #default="scope">
            <img :src="scope.row.img" alt="菜品图片" class="meal-thumb" @click="previewImage(scope.row.img)" />
          </template>
        </el-table-column>
      <el-table-column prop="name" label="膳食名称" min-width="120" />
      <el-table-column prop="category" label="品类" min-width="100">
        <template #default="scope">
          <el-tag>{{ categoryMap[scope.row.category] || scope.row.category }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="pork" label="是否含有猪肉" min-width="80">
        <template #default="scope">
          <el-tag :type="scope.row.pork === '0' ? 'danger' : ''">
            {{ scope.row.pork === '0' ? '含有猪肉' : '不含猪肉' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="time" label="时间" min-width="80">
        <template #default="scope">
          <el-tag :type="scope.row.time === '0' ? 'success' : ( scope.row.time === '1' ? 'warning' : '' )">
            {{ scope.row.time === '0' ? '早餐' : (scope.row.time === '1' ? '午餐' : '晚餐' ) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" min-width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'on' ? 'success' : 'danger'">
            {{ scope.row.status === 'on' ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="scope">
          <el-button type="primary" link @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

      <el-pagination class="table-pagination" background
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        layout="total, prev, pager, next, sizes, jumper"
        :total="total"
        :page-sizes="[5, 10, 20, 50]"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
    </el-card>

    <!-- 添加/编辑膳食弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" align-center @close="resetForm">
      <el-form :model="editForm" :rules="rules" ref="formRef" label-width="150px">

        <el-form-item label="名称" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入膳食名称" />
        </el-form-item>
        <el-form-item label="品类" prop="category">
          <el-select v-model="editForm.category" placeholder="请选择品类">
            <el-option
              v-for="item in categoryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="是否含有猪肉" prop="pork">
          <el-select v-model="editForm.pork" placeholder="请选择是否含有猪肉">
            <el-option label="含有猪肉" value="0" />
            <el-option label="不含猪肉" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间" prop="time">
          <el-select v-model="editForm.time" placeholder="请选择时间">
            <el-option label="早餐" value="0" />
            <el-option label="午餐" value="1" />
            <el-option label="晚餐" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="editForm.status" placeholder="请选择状态">
            <el-option label="启用" value="on" />
            <el-option label="停用" value="off" />
          </el-select>
        </el-form-item>
        <el-button type="primary" size="small" @click="openAvatarDialog" plain style="margin-left: 80px;">上传菜品图片</el-button>
      </el-form>
      <template #footer>
      <input
        ref="avatarInput"
        type="file"
        accept="image/*"
        style="display: none;"
        @change="handleFileChange"
      />
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveFood">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox,ElImageViewer } from 'element-plus'
import { getFoodList, addFood, updateFood, uploadDishImage } from '@/api/diet'
import SvgIcon from '@/components/base/svg-icon/index.vue'

// 图片预览相关状态
const imagePreviewVisible = ref(false)
const selectedImageUrl = ref('')

// 预览图片方法
const previewImage = (url) => {
  selectedImageUrl.value = url
  imagePreviewVisible.value = true
}
const searchQuery = ref('')
const foodList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const dialogVisible = ref(false)
const dialogTitle = ref('')
const editForm = reactive({
  id: null,
  name: '',
  category: '',
  status: '',
  pork: '',
  time: '',
})
const formRef = ref(null)

const categoryOptions = [
  { label: '素菜', value: '素菜' },
  { label: '荤菜', value: '荤菜' },
  { label: '主食', value: '主食' },
  { label: '饮品', value: '饮品' },
]
const categoryMap = {
  素菜: '素菜',
  荤菜: '荤菜',
  主食: '主食',
  饮品: '饮品',
}

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择品类', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  pork: [{ required: true, message: '请选择是否含有猪肉', trigger: 'change' }],
  time: [{ required: true, message: '请选择时间', trigger: 'change' }],
}

const multipleSelection = ref([])
const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

const fetchFoodList = async () => {
  try {
    const params = {
      name: searchQuery.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value,
    }
    const res = await getFoodList(params)
    // 假设返回 { list: [...], total: 100 }
    foodList.value = res.list || []
    total.value = res.total || 0
  } catch (error) {
    ElMessage.error('获取膳食列表失败')
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchFoodList()
}
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchFoodList()
}
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchFoodList()
}
const avatarInput = ref(null)
const openAvatarDialog = () => {
  if (avatarInput.value) {
    avatarInput.value.click()
  }
}
var imgFile = ref()
// 处理文件选择
const handleFileChange = (e) => {
  const file = e.target.files[0];
    console.log(file)
  if (file) {
    // 执行上传前验证

    const isValid = beforeAvatarUpload(file);
    if (isValid) {
      imgFile = file
    }
  }
};

// 上传之前检查
const beforeAvatarUpload = (rawFile) => {
  if (!['image/jpeg', 'image/png'].includes(rawFile.type)) {
    ElMessage.error('请上传JPG或PNG格式的图片');
    return false;
  }
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('图片大小不能超过2MB');
    return false;
  }
  return true;
};

const openAddDialog = () => {
  dialogTitle.value = '添加膳食'
  resetForm()
  dialogVisible.value = true
}
const openEditDialog = (row) => {
  dialogTitle.value = '编辑膳食'
  editForm.dishId = row.dishId
  editForm.name = row.name
  editForm.category = row.category
  editForm.status = row.status
  editForm.pork = row.pork
  editForm.time = row.time
  dialogVisible.value = true
}
const resetForm = () => {
  editForm.dishId = null
  editForm.name = ''
  editForm.category = ''
  editForm.status = ''
  editForm.pork = ''
  editForm.time = ''
  if (formRef.value) formRef.value.clearValidate()
}
const saveFood = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (editForm.dishId) {
        // 编辑
        await updateFood({
          dishId: editForm.dishId,
          name: editForm.name,
          category: editForm.category,
          status: editForm.status,
          pork: editForm.pork,
          time: editForm.time,
        })
        ElMessage.success('修改成功')
      } else {
        // 新增
        await addFood({
          name: editForm.name,
          category: editForm.category,
          status: editForm.status,
          pork: editForm.pork,
          time: editForm.time,
        })
        ElMessage.success('添加成功')
      }
      // 如果有图片才上传
      console.log('imgFile', imgFile)
      if (imgFile) {
        const formData = new FormData();
        formData.append('file', imgFile); // 添加文件
        formData.append('name', editForm.name); // 添加额外参数

        await uploadDishImage(formData).then((response) => {
          ElMessage.success('图片上传成功');

        });
      }
      dialogVisible.value = false
      //imgFile设为空
      imgFile = null
      fetchFoodList()
    } catch (error) {
      console.log(error)
      ElMessage.error('操作失败')
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该膳食吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await post(`/diet/deleteFood`, { id: [row.dishId] } )
        ElMessage.success('删除成功')
        fetchFoodList()
      } catch (error) {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {})
}

const handleBatchDelete = () => {
  if (!multipleSelection.value.length) return
  ElMessageBox.confirm(`确定要删除选中的${multipleSelection.value.length}个膳食吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        const ids = multipleSelection.value.map((item) => item.dishId)
        await post(`/diet/deleteFood`, { id : ids })
        ElMessage.success('批量删除成功')
        fetchFoodList()
      } catch (error) {
        ElMessage.error('批量删除失败')
      }
    })
    .catch(() => {})
}

onMounted(() => {
  fetchFoodList()
})
</script>

<style scoped>
.meal-thumb {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
}
</style>
